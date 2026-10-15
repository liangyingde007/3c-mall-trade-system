"""Rebuild the fictional seed SQL from catalog.json; never connects to a database."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def quoted(value):
    if isinstance(value, (int, float)):
        return str(value)
    return "'" + str(value).replace("'", "''") + "'"

def insert(table, values):
    columns = ', '.join('`' + name + '`' for name in values)
    data = ', '.join(quoted(value) for value in values.values())
    return f'INSERT INTO `{table}` ({columns}) VALUES ({data});'

def build():
    products = json.loads((ROOT / 'runtime/catalog.json').read_text(encoding='utf-8'))
    statements = ['-- Fictional product fixtures. Initialize a NEW demo database only.',
                  'USE `lyd_mall_demo`;', 'SET NAMES utf8mb4;', 'START TRANSACTION;']
    category_ids = {'phone': 900111, 'laptop': 900121, 'headphones': 900131}
    for index, product in enumerate(products, 1):
        sku = product['skuId']
        spu = 911000 + index
        category = category_ids[product['category']]
        group = 951000 + index
        statements.append(insert('pms_sku_info', dict(sku_id=sku, spu_id=spu,
            sku_name=product['name'], sku_desc=product['feature'], catalog_id=category,
            brand_id=900001, sku_default_img=product['image'], sku_title=product['name'],
            sku_subtitle=product['feature'], price=product['price'], sale_count=0)))
        statements.append(insert('pms_attr_group', dict(attr_group_id=group,
            attr_group_name='基本规格', sort=index, descript='虚构演示参数', catelog_id=category)))
        for position, (name, value) in enumerate(product['specs'].items()):
            offset = index * 10 + position
            attr = 941000 + offset
            statements.append(insert('pms_attr', dict(attr_id=attr, attr_name=name,
                attr_type=2, enable=1, catelog_id=category, search_type=0,
                show_desc=1, value_select=value)))
            statements.append(insert('pms_attr_attrgroup_relation', dict(id=971000+offset,
                attr_id=attr, attr_group_id=group, attr_sort=position)))
            statements.append(insert('pms_product_attr_value', dict(id=981000+offset,
                spu_id=spu, attr_id=attr, attr_name=name, attr_value=value,
                attr_sort=position, quick_show=1)))
            statements.append(insert('pms_sku_sale_attr_value', dict(id=991000+offset,
                sku_id=sku, attr_id=attr, attr_name=name, attr_value=value, attr_sort=position)))
        statements.append(insert('wms_ware_sku', dict(id=901000+index, sku_id=sku,
            ware_id=930001, stock=product['stock'], stock_locked=0, sku_name=product['name'])))
    statements.append('COMMIT;')
    (ROOT / 'runtime/seed.sql').write_text('\n'.join(statements) + '\n', encoding='utf-8')
    print(f'Generated fictional seed: {len(products)} products, no database connection.')

if __name__ == '__main__':
    build()
