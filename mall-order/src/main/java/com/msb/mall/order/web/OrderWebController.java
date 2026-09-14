package com.msb.mall.order.web;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.msb.common.constant.OrderConstant;
import com.msb.common.exception.NoStockExecption;
import com.msb.mall.order.config.AlipayTemplate;
import com.msb.mall.order.service.OrderService;
import com.msb.mall.order.vo.OrderConfirmVo;
import com.msb.mall.order.vo.OrderResponseVO;
import com.msb.mall.order.vo.OrderSubmitVO;
import com.msb.mall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    @GetMapping("/toTrade")
    public String toTrade(Model model){
        // TODO 查询订单确认页需要的信息
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("confirmVo",confirmVo);
        return "confirm";
    }

    @PostMapping("/orderSubmit")
    public String orderSubmit(OrderSubmitVO vo, Model model, RedirectAttributes redirectAttributes){
        Integer code = 0;
        OrderResponseVO responseVO = null;
        try{
            responseVO = orderService.submitOrder(vo);
            code = responseVO.getCode();
        }catch (NoStockExecption e){
            code = 2;
        }

        if(code == 0){
            model.addAttribute("orderResponseVO",responseVO);
            // 表示下单操作成功
            return "pay";
        }else{
            System.out.println("code=" + code);
            String msg = "订单失败";
            if(code == 1){
                msg = msg + ":重复提交";
            }else if(code == 2){
                msg = msg + ":锁定库存失败";
            }
            //redirectAttributes.addAttribute("msg",msg);
            redirectAttributes.addFlashAttribute("msg",msg);
            // 表示下单操作失败
            return "redirect:http://order.msb.com/toTrade";
        }
    }

    @GetMapping("/orderPay/returnUrl")
    public String orderPay(@RequestParam(value = "orderSn",required = false) String orderSn,
                           @RequestParam(value = "out_trade_no",required = false) String out_trade_no){
        // TODO 完成相关的支付操作
        System.out.println("orderSn = " + orderSn);
        if(StringUtils.isNotBlank(orderSn)){
            orderService.handleOrderComplete(orderSn);

        }else{
            //orderService.updateOrderStatus(out_trade_no,OrderConstant.OrderStateEnum.TO_SEND_GOODS.getCode());
            orderService.handleOrderComplete(out_trade_no);
        }
        return "list";
    }

    /**
     * 获取订单相关信息
     * 然后跳转到支付页面  tklalf8880@sandbox.com
     * @param orderSn
     * @return
     */
    @GetMapping(value = "/payOrder",produces = "text/html")
    @ResponseBody
    public String payOrder(@RequestParam("orderSn") String orderSn){
        // 根据订单编号查询出相关的订单信息，封装到PayVO中
        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        //System.out.println(pay);
        return pay;
    }
}
