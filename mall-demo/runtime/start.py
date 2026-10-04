"""One ephemeral interview demo: Java + loopback MySQL + loopback Redis.

Every application start creates a NEW, fictional database and Redis instance.
No existing database is imported, altered, restored, dumped, or deleted.
"""
import json
import os
from pathlib import Path
import secrets
import signal
import subprocess
import sys
import tempfile
import time

ROOT=Path(tempfile.mkdtemp(prefix='lyd-mall-demo-'))
DATA=ROOT/'mysql'
DATA.mkdir()
PASSWORD=secrets.token_urlsafe(24)
PROCESSES=[]

def memory(label):
    values={}
    for key, name in [('current','memory.current'),('peak','memory.peak'),('limit','memory.max')]:
        path=Path('/sys/fs/cgroup')/name
        if path.exists(): values[key]=path.read_text().strip()
    rss={}
    for name, process in PROCESSES:
        status=Path(f'/proc/{process.pid}/status')
        if status.exists():
            for line in status.read_text().splitlines():
                if line.startswith('VmRSS:'): rss[name]=int(line.split()[1])*1024
    print('DEMO_MEMORY '+json.dumps({'stage':label,'cgroupBytes':values,'rssBytes':rss}),flush=True)

def stop(signum=None,frame=None):
    for _,process in reversed(PROCESSES):
        if process.poll() is None: process.terminate()
    for _,process in reversed(PROCESSES):
        try: process.wait(timeout=8)
        except subprocess.TimeoutExpired: process.kill()

def run_mysql(statement,defaults=None):
    args=['mysql','--no-defaults' if defaults is None else '--defaults-extra-file='+str(defaults), '--host=127.0.0.1','--port=13306','--user=root','--default-character-set=utf8mb4','--batch']
    result=subprocess.run(args,input=statement.encode('utf-8'),stdout=subprocess.DEVNULL,stderr=subprocess.PIPE,timeout=30)
    if result.returncode: raise RuntimeError('Demo SQL setup failed')

def main():
    signal.signal(signal.SIGTERM,stop)
    signal.signal(signal.SIGINT,stop)
    print('Starting fictional interview demo; state is recreated on every application start.',flush=True)
    subprocess.run(['mysqld','--no-defaults','--initialize-insecure','--user=root','--datadir='+str(DATA),'--log-error='+str(ROOT/'mysql-init.log')],check=True,timeout=90)
    mysql=subprocess.Popen(['mysqld','--no-defaults','--user=root','--datadir='+str(DATA),'--bind-address=127.0.0.1','--port=13306','--socket='+str(ROOT/'mysql.sock'), '--pid-file='+str(ROOT/'mysql.pid'), '--log-error='+str(ROOT/'mysql.log'), '--mysqlx=OFF','--skip-log-bin','--performance-schema=OFF','--innodb-buffer-pool-size=32M','--innodb-log-buffer-size=4M','--key-buffer-size=1M','--max-connections=12','--table-open-cache=64','--table-definition-cache=400','--thread-cache-size=2','--tmp-table-size=4M','--max-heap-table-size=4M'])
    PROCESSES.append(('mysql',mysql))
    for _ in range(60):
        if mysql.poll() is not None: raise RuntimeError('Demo MySQL did not start')
        result=subprocess.run(['mysqladmin','--no-defaults','--host=127.0.0.1','--port=13306','--user=root','ping'],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
        if result.returncode==0: break
        time.sleep(1)
    else: raise RuntimeError('Demo MySQL start timed out')
    schema=Path('/app/runtime/schema.sql').read_text(encoding='utf-8')
    seed=Path('/app/runtime/seed.sql').read_text(encoding='utf-8')
    # Random credentials stay inside this instance; neither command arguments nor logs contain them.
    run_mysql(schema+'\n'+seed+f"\nCREATE USER 'demo'@'127.0.0.1' IDENTIFIED BY '{PASSWORD}'; GRANT SELECT,INSERT,UPDATE ON lyd_mall_demo.* TO 'demo'@'127.0.0.1'; ALTER USER 'root'@'localhost' IDENTIFIED BY '{PASSWORD}';")
    redis=subprocess.Popen(['redis-server','--bind','127.0.0.1','--port','13380','--protected-mode','yes','--save','','--appendonly','no','--maxmemory','24mb','--maxmemory-policy','noeviction','--dir',str(ROOT)])
    PROCESSES.append(('redis',redis))
    for _ in range(30):
        result=subprocess.run(['redis-cli','-h','127.0.0.1','-p','13380','ping'],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
        if result.returncode==0: break
        time.sleep(0.25)
    else: raise RuntimeError('Demo Redis start timed out')
    env=dict(os.environ,DEMO_DB_USER='demo',DEMO_DB_PASSWORD=PASSWORD,DEMO_MYSQL_PORT='13306',DEMO_REDIS_PORT='13380')
    memory('dependencies-ready')
    java=subprocess.Popen(['java','-Xms64m','-Xmx160m','-Xss256k','-XX:MaxMetaspaceSize=96m','-XX:ReservedCodeCacheSize=48m','-XX:MaxDirectMemorySize=24m','-XX:ActiveProcessorCount=2','-Duser.timezone=Asia/Shanghai','-jar','/app/mall-demo.jar'],env=env)
    PROCESSES.append(('java',java))
    next_measurement=time.monotonic()+60
    while all(process.poll() is None for _,process in PROCESSES):
        time.sleep(1)
        if time.monotonic()>=next_measurement:
            memory('running'); next_measurement=time.monotonic()+60
    raise RuntimeError('A demo component exited; stop the whole instance rather than serving partial transactions')

if __name__=='__main__':
    try: main()
    except Exception as error:
        print('Demo startup/runtime failed: '+str(error),file=sys.stderr,flush=True)
        stop()
        sys.exit(1)
