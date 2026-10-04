"""Explicit source allowlist. Run from this module after intentional original-code edits.

The public module builds independently; original private application configuration is
never copied. A digest manifest makes generated copies reviewable.
"""
import argparse
import hashlib
import json
from pathlib import Path

MODULE = Path(__file__).resolve().parents[1]
REPO = MODULE.parent
JAVA = {
    'mall-commons': ['com/msb/common/constant/AuthConstant.java', 'com/msb/common/constant/CartConstant.java', 'com/msb/common/constant/OrderConstant.java', 'com/msb/common/vo/MemberVO.java', 'com/msb/common/utils/R.java'],
    'mall-cart': ['com/msb/mall/Interceptor/AuthInterceptor.java', 'com/msb/mall/vo/Cart.java', 'com/msb/mall/vo/CartItem.java', 'com/msb/mall/vo/SkuInfoVo.java', 'com/msb/mall/feign/ProductFeignService.java', 'com/msb/mall/service/ICartService.java', 'com/msb/mall/service/impl/CartServiceImpl.java', 'com/msb/mall/controller/DemoCartController.java', 'com/msb/mall/controller/DemoWebController.java'],
    'mall-product': ['com/msb/mall/product/entity/SkuInfoEntity.java', 'com/msb/mall/product/dao/SkuInfoDao.java'],
    'mall-order': ['com/msb/mall/order/entity/OrderEntity.java', 'com/msb/mall/order/entity/OrderItemEntity.java', 'com/msb/mall/order/dao/OrderDao.java', 'com/msb/mall/order/dao/OrderItemDao.java', 'com/msb/mall/order/vo/OrderItemVo.java', 'com/msb/mall/order/demo/DemoOrderService.java', 'com/msb/mall/order/demo/DemoOrderExpiry.java', 'com/msb/mall/order/demo/dao/DemoInventoryDao.java'],
}
FILES = []
for module, names in JAVA.items():
    FILES.extend((f'{module}/src/main/java/{name}', f'src/main/java/{name}') for name in names)
FILES.extend([
    ('mall-product/src/main/resources/mapper/product/SkuInfoDao.xml', 'src/main/resources/mapper/product/SkuInfoDao.xml'),
    ('mall-order/src/main/resources/mapper/order/OrderDao.xml', 'src/main/resources/mapper/order/OrderDao.xml'),
    ('mall-order/src/main/resources/mapper/order/OrderItemDao.xml', 'src/main/resources/mapper/order/OrderItemDao.xml'),
])
for name in ['demo/index.html', 'demo/styles.css', 'demo/app.js', 'demo/favicon.svg', *[f'demo-assets/product-0{i}.svg' for i in (1,2,3)]]:
    FILES.append(('mall-cart/src/main/resources/static/' + name, 'src/main/resources/static/' + name))

def main():
    parser=argparse.ArgumentParser()
    parser.add_argument('--check',action='store_true')
    args=parser.parse_args()
    records=[]
    for source, destination in FILES:
        original=(REPO/source).read_bytes().replace(b'\r\n',b'\n')
        content=('\n'.join(line.rstrip() for line in original.decode('utf-8').splitlines())+'\n').encode('utf-8')
        if destination=='src/main/resources/static/demo/index.html':
            # This notice applies only to the ephemeral hosted module, not the local D4-B database.
            text=content.decode('utf-8')
            notice='<p class="demo-notice" role="note">线上演示使用临时数据：服务休眠或重启后，购物车、订单和库存会重置。</p>'
            assert text.count('<main')==1
            start=text.index('>',text.index('<main'))+1
            content=(text[:start]+'\n'+notice+text[start:]).encode('utf-8')
        output=MODULE/destination
        if args.check:
            assert output.read_bytes()==content, 'Outdated source copy: '+destination
        else:
            output.parent.mkdir(parents=True,exist_ok=True)
            output.write_bytes(content)
        records.append({'source':source,'destination':destination,'sourceSha256':hashlib.sha256(original).hexdigest(),'sha256':hashlib.sha256(content).hexdigest()})
    data={'purpose':'Original-code allowlist for the public single-JVM demo; no production configuration', 'textLineEndings':'LF before hashing; destination trailing whitespace removed', 'files':records}
    if args.check:
        assert json.loads((MODULE/'source-manifest.json').read_text(encoding='utf-8'))==data
    else:
        (MODULE/'source-manifest.json').write_text(json.dumps(data,indent=2,ensure_ascii=False)+'\n',encoding='utf-8')
    print(f'Original source/resource allowlist verified: {len(records)} files')

if __name__=='__main__': main()
