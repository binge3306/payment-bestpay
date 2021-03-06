package org.thlws.payment.bestpay.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.thlws.payment.bestpay.api.BestpayApi;
import org.thlws.payment.bestpay.entity.request.BarcodePayRequest;
import org.thlws.payment.bestpay.entity.request.OrderRefundRequest;
import org.thlws.payment.bestpay.entity.request.OrderReverseRequest;
import org.thlws.payment.bestpay.entity.request.QueryOrderRequest;
import org.thlws.payment.bestpay.entity.response.OrderRefundResponse;
import org.thlws.payment.bestpay.entity.response.OrderResultResponse;
import org.thlws.payment.bestpay.entity.response.OrderReverseResponse;
import org.thlws.payment.bestpay.sign.MD5;
import org.thlws.payment.bestpay.utils.ThlwsJsonUtil;

import java.util.Map;

/**
 * 电信天翼支付 核心Class，提供支付相关接口
 * 核心参数<br>
 * <ul>
 *     <li>merchantId -商户号</li>
 *     <li>key -商户数据 KEY</li>
 *     <li>merchantPwd 商户密码(又称 交易key),退款和撤销时必须</li>
 * </ul>
 *
 * @see <a href="https://www.bestpay.com.cn/play/start">https://www.bestpay.com.cn/play/start</a>
 * Created by HanleyTang on 2018/1/5
 *
 * @author Hanley[hanley@thlws.com]
 * @version 1.0
 */
public class BestpayCore implements BestpayApi{

	private static final Log log = LogFactory.get();

	/**
	 * 天翼支付-付款码支付.
	 *
	 * @param request 支付参数对象 {@link BarcodePayRequest}
	 * @param key   商户数据Key,签名需要
	 * @return 支付结果对象 {@link OrderResultResponse}
	 */
	public static OrderResultResponse barcode(BarcodePayRequest request, String key) throws Exception{

		OrderResultResponse out = null;

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("MERCHANTID=").append(request.getMerchantId());
			sb.append("&ORDERNO=").append(request.getOrderNo());
			sb.append("&ORDERREQNO=").append(request.getOrderReqNo());
			sb.append("&ORDERDATE=").append(request.getOrderDate());
			sb.append("&BARCODE=").append(request.getBarcode());
			sb.append("&ORDERAMT=").append(request.getOrderAmt());
			sb.append("&KEY=").append(key);
			String mac = MD5.sign(sb.toString());

			Map<String, Object> map = BeanUtil.beanToMap(request,false,true);
			map.put("mac", mac);
			log.info("天翼扫码支付请求数据[barcode]->request:\n {}", ThlwsJsonUtil.format(map));
			String result = HttpUtil.get(barcode, map);
			log.info("天翼扫码支付返回数据[barcode]->response \n : {}",ThlwsJsonUtil.format(result));
			out = JSONUtil.toBean(result, OrderResultResponse.class);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return out;
	}


	/**
	 * 查询支付订单
	 *
	 * @param request 查询参数对象 {@link QueryOrderRequest}
	 * @param key   商户数据Key,签名需要
	 * @return 支付结果对象 {@link OrderResultResponse}
	 */
	public static OrderResultResponse query(QueryOrderRequest request, String key) throws Exception {
		
		OrderResultResponse out = null;

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("MERCHANTID=").append(request.getMerchantId());
			sb.append("&ORDERNO=").append(request.getOrderNo());
			sb.append("&ORDERREQNO=").append(request.getOrderReqNo());
			sb.append("&ORDERDATE=").append(request.getOrderDate());
			sb.append("&KEY=").append(key);

			String mac = MD5.sign(sb.toString());
			Map<String, Object> map = BeanUtil.beanToMap(request,false,true);
			map.put("mac", mac);
			log.info("翼支付查询请求数据[query]->request:\n {}", ThlwsJsonUtil.format(map));
			String result = HttpUtil.get(query, map);
			log.info("翼支付查询返回数据[query]->response \n : {}",ThlwsJsonUtil.format(result));
			out = JSONUtil.toBean(result, OrderResultResponse.class);

		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return out;
	}


	/**
	 * 商户退款,须商户密码 OrderRefundRequest.merchantPwd
	 *
	 * @param request 退款参数对象 {@link OrderRefundRequest}
	 * @param key   商户数据Key,签名需要
	 * @return 退款结果对象 {@link OrderRefundResponse}
	 */
	public static OrderRefundResponse refund(OrderRefundRequest request, String key)  throws Exception {
		
		OrderRefundResponse out = null;

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("MERCHANTID=").append(request.getMerchantId());
			sb.append("&MERCHANTPWD=").append(request.getMerchantPwd());
			sb.append("&OLDORDERNO=").append(request.getOldOrderNo());
			sb.append("&OLDORDERREQNO=").append(request.getOldOrderReqNo());
			sb.append("&REFUNDREQNO=").append(request.getRefundReqNo());
			sb.append("&REFUNDREQDATE=").append(request.getRefundReqDate() );
			sb.append("&TRANSAMT=").append(request.getTransAmt());
			sb.append("&LEDGERDETAIL=").append(request.getLedgerDetail());
			sb.append("&KEY=").append(key);

			String mac = MD5.sign(sb.toString());
			Map<String, Object> map = BeanUtil.beanToMap(request,false,true);
			map.put("mac", mac);
			log.info("翼支付退款请求数据[refund]->request:\n {}", ThlwsJsonUtil.format(map));
			String result = HttpUtil.post(refund, map);
			log.info("翼支付退款返回数据[refund]->response \n : {}",ThlwsJsonUtil.format(result));
			out = JSONUtil.toBean(result, OrderRefundResponse.class);

		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return out;
		
	}

	/**
	 * 撤销支付,须商户密码 OrderReverseRequest.merchantPwd
	 *
	 * @param request 撤销参数对象 {@link OrderReverseRequest}
	 * @param key   商户数据Key,签名需要
	 * @return 撤销结果对象 {@link OrderReverseResponse}
	 */
	public static OrderReverseResponse reverse(OrderReverseRequest request, String key)  throws Exception {
		
		
		OrderReverseResponse out = null;

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("MERCHANTID=").append(request.getMerchantId());
			sb.append("&MERCHANTPWD=").append(request.getMerchantPwd());
			sb.append("&OLDORDERNO=").append(request.getOldOrderNo());
			sb.append("&OLDORDERREQNO=").append(request.getOldOrderReqNo());
			sb.append("&REFUNDREQNO=").append(request.getRefundReqNo());
			sb.append("&REFUNDREQDATE=").append(request.getRefundReqDate());
			sb.append("&TRANSAMT=").append(request.getTransAmt());
			sb.append("&KEY=").append(key);

			String mac = MD5.sign(sb.toString());
			Map<String, Object> map = BeanUtil.beanToMap(request,false,true);
			map.put("mac", mac);
			log.info("翼支付撤销请求数据[reverse]->request:\n {}", ThlwsJsonUtil.format(map));
			String result = HttpUtil.post(reverse, map);
			log.info("翼支付撤销返回数据[reverse]->response \n : {}",ThlwsJsonUtil.format(result));
			out = JSONUtil.toBean(result, OrderReverseResponse.class);

		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return out;
		
	}
}
