'use strict';
const $ = selector => document.querySelector(selector);
const money = value => new Intl.NumberFormat('zh-CN', {style:'currency',currency:'CNY'}).format(Number(value));
const categories = {900111:'手机',900121:'笔记本',900131:'耳机'};
let state = {products:[],cart:{items:[],totalAmount:0,checkCountNum:0},busy:false};
let detailTrigger = null;
let orderQuote = null;

function status(message, error=false) {
  $('#page-status').textContent = message;
  $('#page-status').classList.toggle('status-error',error);
}

async function request(path, method='GET', payload) {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(),12000);
  try {
    const response = await fetch(path,{method,credentials:'same-origin',signal:controller.signal,
      headers:payload === undefined ? {} : {'Content-Type':'application/json'},
      body:payload === undefined ? undefined : JSON.stringify(payload)});
    const result = await response.json();
    if (!response.ok || (result.code !== undefined && result.code !== 0)) {
      throw new Error(result.message || result.msg || '暂时无法完成操作，请稍后重试');
    }
    return result;
  } catch (error) {
    if (error.name === 'AbortError' || error instanceof TypeError) {
      throw new Error('服务暂时无法连接，请稍后重试');
    }
    throw error;
  } finally {clearTimeout(timeout);}
}

function setBusy(busy) {
  state.busy = busy;
  document.querySelectorAll('#product-grid button,#cart-items button,#cart-items input').forEach(el => el.disabled=busy);
  $('#cart-items').setAttribute('aria-busy',String(busy));
  $('#checkout-button').disabled=busy || !state.cart.checkCountNum;
  document.querySelectorAll('#order-list button,#refresh-orders,#submit-order').forEach(el=>el.disabled=busy);
  $('#submit-order').disabled=busy || !orderQuote;
}

function element(tag, className, text) {
  const node=document.createElement(tag);
  if (className) node.className=className;
  if (text !== undefined) node.textContent=text;
  return node;
}

function imageUrl(path) {
  return /^\/demo-assets\/product-0[123]\.svg$/.test(path) ? path : '/demo-assets/product-01.svg';
}

function renderProducts() {
  const grid=$('#product-grid'); grid.replaceChildren();
  for (const product of state.products) {
    const fragment=$('#product-template').content.cloneNode(true);
    const card=fragment.querySelector('article'); card.dataset.sku=String(product.skuId);
    card.querySelector('img').src=imageUrl(product.skuDefaultImg);
    card.querySelector('.product-category').textContent=categories[product.catalogId] || '数码商品';
    card.querySelector('h3').textContent=product.skuTitle;
    card.querySelector('.product-subtitle').textContent=product.skuSubtitle;
    card.querySelector('.product-price').textContent=money(product.price);
    card.querySelector('[data-action="detail"]').setAttribute('aria-label',`查看${product.skuTitle}的规格详情`);
    card.querySelector('[data-action="add"]').setAttribute('aria-label',`将${product.skuTitle}加入购物车`);
    grid.append(fragment);
  }
  $('#product-count').textContent=`${state.products.length} 件示例商品`;
  grid.setAttribute('aria-busy','false');
}

function renderCart() {
  const items=$('#cart-items'); items.replaceChildren();
  if (!state.cart.items.length) {
    const empty=element('div','empty-cart');
    const mark=element('div','empty-mark','[ 0 ]'); mark.setAttribute('aria-hidden','true');
    empty.append(mark,element('h3',null,'购物车还是空的'),element('p',null,'把想看的商品加入购物车。')); items.append(empty);
  }
  for (const item of state.cart.items) {
    const row=element('article','cart-item'); row.dataset.sku=String(item.skuId);
    const top=element('div','cart-item-top');
    const check=element('input','cart-check'); check.type='checkbox'; check.checked=item.check;
    check.dataset.action='check'; check.setAttribute('aria-label',`选中${item.title}`);
    const heading=element('div','cart-item-heading');
    heading.append(element('h3',null,item.title),element('p','cart-item-meta',`${money(item.price)} / 件`));
    const remove=element('button','remove-item','移除'); remove.type='button'; remove.dataset.action='remove';
    remove.setAttribute('aria-label',`移除${item.title}`); top.append(check,heading,remove);
    const bottom=element('div','cart-item-bottom');
    const label=element('label','quantity-field'); label.append(element('span',null,'数量'));
    const quantity=element('input'); quantity.type='number'; quantity.min='1'; quantity.max='20'; quantity.step='1';
    quantity.value=String(item.count); quantity.dataset.action='quantity'; quantity.setAttribute('aria-label',`${item.title}的数量`);
    label.append(quantity); bottom.append(label,element('strong','item-total',money(item.totalPrice)));
    row.append(top,bottom); items.append(row);
  }
  $('#selected-count').textContent=`${state.cart.checkCountNum} 件`;
  $('#cart-total').textContent=money(state.cart.totalAmount);
  $('#cart-badge').textContent=String(state.cart.items.reduce((sum,item)=>sum+item.count,0));
  items.setAttribute('aria-busy','false');
  setBusy(state.busy);
}

async function mutate(path, method, payload, message, focus) {
  if (state.busy) return;
  setBusy(true);
  try {
    const result=await request(path,method,payload); state.cart=result.cart; renderCart(); status(message);
  } catch(error) {status(error.message,true); renderCart();}
  finally {
    setBusy(false);
    if (focus) document.querySelector(`#cart-items [data-sku="${focus.sku}"] [data-action="${focus.action}"]`)?.focus({preventScroll:true});
  }
}

async function showDetail(sku, trigger) {
  if (state.busy) return;
  setBusy(true); detailTrigger=trigger; status('正在读取商品规格…');
  try {
    const result=await request(`/demo/api/products/${sku}`); const item=result.item;
    const content=$('#detail-content'); content.replaceChildren();
    const layout=element('div','detail-layout');
    const image=element('img','detail-image'); image.src=imageUrl(item.info.skuDefaultImg); image.alt=''; image.width=160; image.height=160;
    const copy=element('div'); const title=element('h2',null,item.info.skuTitle); title.id='detail-title';
    copy.append(title,element('p','detail-price',money(item.info.price)),element('p','detail-note','虚构示例商品，规格用于演示。'));
    layout.append(image,copy); content.append(layout);
    const specs=element('dl','specs');
    for (const group of item.baseAttrs || []) {
      for (const attr of group.baseAttrs || []) {
        const row=element('div','spec-row'); row.append(element('dt',null,attr.attrName),element('dd',null,attr.attrValue)); specs.append(row);
      }
    }
    if (!specs.childElementCount) {
      const row=element('div','spec-row'); row.append(element('dt',null,'规格'),element('dd',null,'暂无更多示例规格')); specs.append(row);
    }
    content.append(specs); $('#product-dialog').showModal(); $('#close-dialog').focus(); status('商品规格已加载');
  } catch(error) {status(error.message,true);}
  finally {setBusy(false);}
}

$('#product-grid').addEventListener('click',event => {
  const button=event.target.closest('button[data-action]'); if (!button || state.busy) return;
  const sku=button.closest('[data-sku]').dataset.sku;
  if (button.dataset.action === 'detail') showDetail(sku,button);
  else mutate('/demo/api/cart','POST',{skuId:Number(sku),quantity:1},'已加入购物车');
});

$('#cart-items').addEventListener('click',event => {
  const button=event.target.closest('button[data-action="remove"]'); if (!button || state.busy) return;
  const sku=button.closest('[data-sku]').dataset.sku;
  mutate(`/demo/api/cart/${sku}`,'DELETE',undefined,'商品已移除');
});

$('#cart-items').addEventListener('change',event => {
  const input=event.target; if (!input.matches('input[data-action]') || state.busy) return;
  const sku=input.closest('[data-sku]').dataset.sku;
  if (input.dataset.action === 'check') {
    mutate(`/demo/api/cart/${sku}`,'PATCH',{checked:input.checked},'已更新勾选商品',{sku,action:'check'});
  } else {
    if (!/^[1-9]\d*$/.test(input.value) || Number(input.value)>20) {
      input.value=String(state.cart.items.find(item=>String(item.skuId)===sku).count);
      status('数量应为 1 到 20 之间的整数',true); return;
    }
    mutate(`/demo/api/cart/${sku}`,'PATCH',{quantity:Number(input.value)},'商品数量已更新',{sku,action:'quantity'});
  }
});

$('#close-dialog').addEventListener('click',()=>$('#product-dialog').close());
$('#product-dialog').addEventListener('close',()=>detailTrigger?.focus({preventScroll:true}));

async function load() {
  $('#retry').hidden=true; status('正在读取商品与购物车…'); setBusy(true);
  try {
    const cart=await request('/demo/api/cart'); state.cart=cart.cart; renderCart();
    const catalog=await request('/demo/api/catalog'); state.products=catalog.products; renderProducts();
    status('商品与购物车已加载');
  } catch(error) {status(error.message,true); $('#retry').hidden=false;}
  finally {setBusy(false);}
}
$('#retry').addEventListener('click',load);
load();

function orderStatus(message,error=false) {
  $('#order-status').textContent=message;
  $('#order-status').classList.toggle('status-error',error);
}

function renderOrders(orders) {
  const list=$('#order-list'); list.replaceChildren();
  if (!orders.length) { list.append(element('p','no-orders','还没有演示订单。勾选商品后可以体验下单。')); return; }
  for (const order of orders) {
    const card=element('article','order-card'); card.dataset.order=order.orderSn;
    const header=element('div','order-card-header');
    const info=element('div'); info.append(element('h3',null,`订单 ${order.orderSn}`),element('p','order-time',new Date(order.createdAt).toLocaleString('zh-CN')));
    header.append(info,element('span',order.status===4?'order-badge closed':'order-badge',order.statusLabel)); card.append(header);
    const lines=element('ul','order-lines');
    for (const item of order.items) { lines.append(element('li',null,`${item.title} × ${item.quantity} · ${money(item.lineTotal)}`)); }
    const footer=element('div','order-card-footer'); footer.append(element('strong',null,`合计 ${money(order.totalAmount)}`));
    if (order.status===0) {
      const cancel=element('button','secondary-button','取消演示订单'); cancel.type='button'; cancel.dataset.action='cancel';
      cancel.setAttribute('aria-label',`取消演示订单 ${order.orderSn}`); footer.append(cancel);
    } else { footer.append(element('span','order-release-note','示例库存已释放')); }
    card.append(lines,footer); list.append(card);
  }
}

async function loadOrders() {
  orderStatus('正在读取演示订单…');
  try { renderOrders(await request('/demo/api/orders')); orderStatus('演示订单已更新'); }
  catch(error) { orderStatus(error.message,true); }
}

$('#refresh-orders').addEventListener('click',()=>{if (!state.busy) loadOrders();});
$('#checkout-button').addEventListener('click',async()=>{
  if (state.busy || !state.cart.checkCountNum) return;
  orderQuote=null; setBusy(true); status('正在确认商品与演示金额…');
  try {
    orderQuote=await request('/demo/api/checkout','POST',{});
    const lines=$('#checkout-lines'); lines.replaceChildren();
    for (const item of orderQuote.items) {
      const row=element('div','checkout-line'); row.append(element('span',null,`${item.title} × ${item.quantity}`),element('strong',null,money(item.lineTotal))); lines.append(row);
    }
    $('#checkout-total').textContent=money(orderQuote.totalAmount);
    $('#checkout-status').textContent='确认有效期为 10 分钟。'; $('#checkout-status').classList.remove('status-error');
    $('#checkout-dialog').showModal(); $('#submit-order').focus(); status('订单已确认，尚未创建');
  } catch(error) {status(error.message,true);}
  finally {setBusy(false); if ($('#checkout-dialog').open && orderQuote) $('#submit-order').focus();}
});

$('#close-checkout').addEventListener('click',()=>$('#checkout-dialog').close());
$('#checkout-dialog').addEventListener('close',()=>{$('#checkout-button').focus({preventScroll:true});});
$('#submit-order').addEventListener('click',async()=>{
  if (state.busy || !orderQuote) return;
  setBusy(true); $('#checkout-status').textContent='正在创建演示订单…';
  try {
    const order=await request('/demo/api/orders','POST',{token:orderQuote.token});
    orderQuote=null; $('#checkout-dialog').close(); status('演示订单已创建，无需付款');
    await loadOrders(); orderStatus('演示订单已创建，示例库存已预留。可以取消体验库存释放。');
    $('#orders').scrollIntoView({behavior:'smooth',block:'start'});
  } catch(error) {
    orderQuote=null; $('#checkout-status').textContent=error.message; $('#checkout-status').classList.add('status-error');
  } finally {setBusy(false); $('#submit-order').disabled=!orderQuote;}
});

$('#order-list').addEventListener('click',async event=>{
  const button=event.target.closest('button[data-action="cancel"]'); if (!button || state.busy) return;
  const sn=button.closest('[data-order]').dataset.order; setBusy(true);
  try { await request(`/demo/api/orders/${encodeURIComponent(sn)}/cancel`,'POST',{}); await loadOrders(); orderStatus('演示订单已关闭，示例库存已释放'); }
  catch(error) {orderStatus(error.message,true);}
  finally {setBusy(false);}
});
loadOrders();
