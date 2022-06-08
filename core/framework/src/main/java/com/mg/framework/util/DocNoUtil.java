package com.mg.framework.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.mg.framework.exception.MgException;
import com.mg.framework.model.ResultModel;
import com.mg.framework.response.ResponseUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 获取下一个单据编码
 *
 * @author Vick
 * @since 2020/10/19
 */
@Slf4j
@Component
public class DocNoUtil {

    /**
     * 订货入库单流水号 redis key
     */
    public static final String INCREMENT_MOS_PREFIX_KEY = "increment:mos:prefix:%s";
    /**
     * 单据中缀年份格式
     */
    public static final String DOC_MIDDLE_DATE_FORMAT = "yyMMdd";
    /**
     * 单据后缀流水位数
     */
    public static final int DOC_POSTFIX_SEQUENCE_NO_LENGTH = 8;
    /**
     * 返利规则单据中缀年份格式
     */
    public static final String DOC_MIDDLE_DATE_ALL_YEAR_FORMAT = "yyyyMMdd";
    /**
     * 反流规则单据后缀流水位数
     */
    public static final int DOC_POSTFIX_SEQUENCE_NO_LENGTH_THREE = 3;
    private static DocNoUtil docNoUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String mosBaseUrlKey = "mos.base-url";

    private static String mosBaseUrl;

    @Resource
    private Environment environment;

    /**
     * 获取下一个单据编码
     * <p>
     * 每日24点会将 DocTypeEnum 中的流水号归零
     * 归零任务使用 xxl job （shop service）
     * 流水号基于redis key实现,前缀为 increment:mos:prefix:
     *
     * @param docType 单据类型前缀
     * @return 下一个单据编码
     */
    public static Long getNextDocNo(DocTypeEnum docType) {
        String docNo = "";
        String url = mosBaseUrl + "/mos/commonservice/common/getDocNo?prefix=" + docType.toString();
        log.info("调取MOS获取订单编号接口开始，{}", JSONObject.toJSONString(docType));
        try {
            String resultStr = HttpUtil.sendGet(url,"applicationSecretKey", "0f61ec6e11d0437ba0c966cabb22f3fc");
            ResultModel resultModel = JSONObject.parseObject(resultStr, ResultModel.class);
            log.info("调取MOS获取订单编号接口返回" + resultStr);
            if (resultModel.getCode() == -1) {
                log.error("调取MOS获取订单编号接口失败，请联系管理员:" + resultModel.getMsg());
                throw new MgException(resultModel.getCode(),resultModel.getMsg());
            }
            docNo = resultModel.getData().toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MgException(-1,"调取MOS获取订单编号接口出错");
        }
        return Long.valueOf(docNo);
    }

    /**
     * 中台和mos的redis不是同一个
     * @param docType
     * @return
     */
    public static Long getNextDocNoBYSaas(DocTypeEnum docType) {
            StringBuffer orderNo = new StringBuffer();
            orderNo.append(docType.getKey());
            //拼接日期
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern(DOC_MIDDLE_DATE_FORMAT));
            orderNo.append(dateStr);
            //拼接流水
            String incrementKey = String.format(INCREMENT_MOS_PREFIX_KEY, docType.getKey());
            Long incrementId = docNoUtil.redisTemplate.opsForValue().increment(incrementKey, 1);
            String incrementCode = MyStringUtil.zerofill(incrementId, DOC_POSTFIX_SEQUENCE_NO_LENGTH);
            orderNo.append(incrementCode);
            return Long.valueOf(orderNo.toString());
        }

    /**
     * 返利规则获取下一个单据编码的流水码
     * <p>
     * 每日24点会将 DocTypeEnum 中的流水号归零
     * 归零任务使用 xxl job （shop service）
     * 流水号基于redis key实现,前缀为 increment:mos:prefix:
     *
     * @param prefix 单据类型前缀
     * @return 下一个单据编码
     */
    public static String getNextDocNo(String prefix) {
        StringBuffer orderNo = new StringBuffer();
        orderNo.append(prefix);
        //拼接日期
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern(DOC_MIDDLE_DATE_FORMAT));
        orderNo.append(dateStr);
        //拼接流水
        String incrementKey = String.format(INCREMENT_MOS_PREFIX_KEY, prefix);
        Long incrementId = docNoUtil.redisTemplate.opsForValue().increment(incrementKey, 1);
        String incrementCode = MyStringUtil.zerofill(incrementId, DOC_POSTFIX_SEQUENCE_NO_LENGTH_THREE);
        orderNo.append(incrementCode);
        return orderNo.toString();
    }

    /**
     * 获取下一个单据编码
     *
     * @param docPrefix          单据类型前缀
     * @param currentDayMaxDocNo 当日最新编码
     * @return 下一个单据编码
     */
    @Deprecated
    public static Long getNextDocNo(String docPrefix, String currentDayMaxDocNo) {
        return getNextDocNo(docPrefix, currentDayMaxDocNo, DOC_POSTFIX_SEQUENCE_NO_LENGTH, DOC_MIDDLE_DATE_FORMAT);
    }

    /**
     * 获取下一个单据编码
     *
     * @param docPrefix           单据类型前缀
     * @param currentDayMaxDocNo  当日最新编码
     * @param sequenceNoLength    流水号位数
     * @param docMiddleDateFormat 单据中间日期格式
     * @return 下一个单据编码
     */
    @Deprecated
    public static Long getNextDocNo(String docPrefix, String currentDayMaxDocNo, int sequenceNoLength, String docMiddleDateFormat) {
        StringBuilder nextPurchaseNoBuilder = new StringBuilder();
        if (currentDayMaxDocNo != null) {
            //单据编码流水号位
            Integer sequenceNo = Integer.valueOf(currentDayMaxDocNo.substring(currentDayMaxDocNo.length() - sequenceNoLength));
            sequenceNo += 1;
            if (String.valueOf(sequenceNo).length() > sequenceNoLength) {
                ResponseUtil.fail("今日单据流水号已耗尽");
            }
            nextPurchaseNoBuilder.append(currentDayMaxDocNo, 0, currentDayMaxDocNo.length() - sequenceNoLength);
            for (int i = 0; i < sequenceNoLength - String.valueOf(sequenceNo).length(); i++) {
                nextPurchaseNoBuilder.append("0");
            }
            nextPurchaseNoBuilder.append(sequenceNo);
        } else {
            nextPurchaseNoBuilder.append(docPrefix)
                    .append(LocalDate.now().format(DateTimeFormatter.ofPattern(docMiddleDateFormat)));
            for (int i = 1; i < sequenceNoLength; i++) {
                nextPurchaseNoBuilder.append("0");
            }
            nextPurchaseNoBuilder.append("1");
        }
        return Long.valueOf(nextPurchaseNoBuilder.toString());
    }

    @PostConstruct
    public void init() {
        docNoUtil = this;
        docNoUtil.redisTemplate = this.redisTemplate;
        mosBaseUrl = environment.getProperty(mosBaseUrlKey);
    }

    @Getter
    public enum DocTypeEnum {
        /******************************** 总部相关单据 ************************************
         /**
         * 采购订单
         */
        NATIONAL_PURCHASE_DOC(200),
        /**
         * 采购退货单
         */
        NATIONAL_PURCHASE_RETURN_DOC(201),
        /**
         * 到货通知单
         */
        NATIONAL_ARRIVE_NOTICE_DOC(202),
        /**
         * 到货验收单
         */
        NATIONAL_ARRIVE_NOTICE_ACCEPT_DOC(203),
        /**
         * 采购入库单
         */
        NATIONAL_IN_STOCK_DOC_PURCHASE(204),
        /**
         * 门店退货入库单
         */
        NATIONAL_IN_STOCK_DOC_SHOP_RETURN(205),
        /**
         * 盘盈入库单
         */
        NATIONAL_IN_STOCK_DOC_INVENTORY_PROFIT(206),
        /**
         * 其他入库单
         */
        NATIONAL_IN_STOCK_DOC_OTHER(207),
        /**
         * 平台退货入库(平台零售出库单的退货入库单)
         */
        NATIONAL_IN_STOCK_DOC_PLATFORM_RETAIL_RETURN(223),
        /**
         * 组合商品入库
         */
        NATIONAL_IN_STOCK_DOC_COMBINATION_SKU(225),
        /**
         * 配送出库单
         */
        NATIONAL_OUT_STOCK_DOC_DELIVERY(208),
        /**
         * 采购退货出库单
         */
        NATIONAL_OUT_STOCK_DOC_PURCHASE_RETURN(209),
        /**
         * 盘损出库单
         */
        NATIONAL_OUT_STOCK_DOC_INVENTORY_LOSS(210),
        /**
         * 其他出库单
         */
        NATIONAL_OUT_STOCK_DOC_OTHER(211),
        /**
         * 零售配货出库单
         */
        NATIONAL_OUT_STOCK_DOC_RETAIL_DELIVERY(218),
        /**
         * 盘点计划
         */
        NATIONAL_INVENTORY_PLAN_DOC(212),
        /**
         * 盘点单
         */
        NATIONAL_INVENTORY_DOC(213),
        /**
         * 收货差异单
         */
        NATIONAL_RECEIVE_GOODS_DISCREPANCY_DOC(214),
        /**
         * 召回计划单
         */
        NATIONAL_CALLBACK_PLAN_DOC(215),
        /**
         * 社群出库
         */
        NATIONAL_OUT_STOCK_DOC_COMMUNITY(219),
        /**
         * 平台零售出库
         */
        NATIONAL_OUT_STOCK_DOC_PLATFORM_RETAIL(220),

        /**
         * 货补出库
         */
        NATIONAL_OUT_STOCK_DOC_REPLENISHMENT(221),
        /**
         * 社群货补出库
         */
        NATIONAL_OUT_STOCK_DOC_COMMUNITY_REPLENISHMENT(222),
        /**
         * 平台分销出库(平台代销出库)
         */
        NATIONAL_OUT_STOCK_DOC_PLATFORM_CONSIGNMENT(224),
        /**
         * 运营商合约套餐商品出库
         */
        NATIONAL_OUT_STOCK_DOC_CONTRACT_PACKAGE(226),
        /**
         * 运营商订货配送出库
         */
        NATIONAL_OUT_STOCK_DOC_DISTRIBUTOR_ORDER_GOODS(227),
        /******************************** 门店相关单据 ************************************
         /**
         * 首铺货单
         */
        SHOP_FIRST_GOODS_ORDER_DOC(100),
        /**
         * 门店订货单
         */
        SHOP_GOODS_ORDER_DOC(101),
        /**
         * 紧急订货单
         */
        SHOP_GOODS_ORDER_DOC_EMERGENCY(102),
        /**
         * 物料订货单
         */
        SHOP_GOODS_ORDER_DOC_MATERIALS(103),
        /**
         * 包裹单
         */
        SHOP_GOODS_ORDER_DOC_PACKAGE(104),
        /**
         * 门店退货单
         */
        SHOP_GOODS_ORDER_RETURN_DOC(105),
        /**
         * 销售单
         */
        SHOP_SALES_ORDER_DOC(106),
        /**
         * 销售退货单
         */
        SHOP_SALES_ORDER_RETURN_DOC(107),
        /**
         * 订货入库单
         */
        SHOP_IN_STOCK_DOC_GOODS_ORDER(108),
        /**
         * 退货入库单
         */
        SHOP_IN_STOCK_DOC_RETURN_GOODS(109),
        /**
         * 调拨入库单
         */
        SHOP_IN_STOCK_DOC_ALLOCATE(110),
        /**
         * 盘盈入库单
         */
        SHOP_IN_STOCK_DOC_INVENTORY_PROFIT(111),
        /**
         * 其他入库单
         */
        SHOP_IN_STOCK_DOC_OTHER(112),
        /**
         * 寄存入库单
         */
        SHOP_IN_STOCK_DOC_DEPOSIT(113),
        /**
         * 销售出库单
         */
        SHOP_OUT_STOCK_DOC_SALES(114),
        /**
         * 退货出库单
         */
        SHOP_OUT_STOCK_DOC_RETURN_GOODS(115),
        /**
         * 调拨出库单
         */
        SHOP_OUT_STOCK_DOC_ALLOCATE(116),
        /**
         * 盘损出库单
         */
        SHOP_OUT_STOCK_DOC_INVENTORY_LOSS(117),
        /**
         * 报损出库单
         */
        SHOP_OUT_STOCK_DOC_REPORT_LOSS(118),
        /**
         * 其他出库单
         */
        SHOP_OUT_STOCK_DOC_OTHER(119),
        /**
         * 寄存提取出库单
         */
        SHOP_OUT_STOCK_DOC_DEPOSIT_EXTRACTION(120),
        /**
         * 盘点单
         */
        SHOP_INVENTORY_DOC(121),
        /**
         * 收货差异单
         */
        SHOP_RECEIVE_GOODS_DISCREPANCY_DOC(122),

        /**
         * 自营商品入库单
         */
        SHOP_IN_STOCK_DOC_SKU_SELF(125),

        /******************************** 财务相关单据 ************************************
         *
         */
        /**
         * 供应商发票单
         */
        SUPPLIER_INVOICE(301),
        /**
         * 供应商勾兑单
         */
        SUPPLIER_INVOICE_BLEND(302),
        /**
         * 供应商付款单
         */
        SUPPLIER_PAYMENT(303),
        /**
         * 供应商对账单
         */
        SUPPLIER_STATEMENT(304),
        /**
         * 供应商付款记录单
         */
        SUPPLIER_PAYMENT_PAYS(305),
        /**
         * 供应商费用
         */
        SUPPLIER_COST(306),

        /**
         * 向门店收款单
         */
        SHOP_PAY_RECIEVE(307),

        /**
         * 向门店付款单
         */
        SHOP_PAY_RETURN(308),

        /**
         * 费用核算单
         */
        COST_ACCOUNTING_RULE(309),


        /******************************** POS相关单据 ************************************/

        /**
         * pos订单
         */
        POS_ORDER(401),

        /**
         * pos退货单
         */
        POS_RETURN_ORDER(402),

        /**
         * 配货单
         */
        SALE_DISTRIBUTION_BILL(403),

        /******************************** 社交电商相关单据 ************************************/

        /**
         * 005订货单
         */
        SOCIAL_COMMERCE_005_PURCHASE_DOC(501),

        /**
         * 005提货单
         */
        SOCIAL_COMMERCE_005_PICKUP_DOC(502),

        /**
         * 005出货单
         */
        SOCIAL_COMMERCE_005_SALES_DOC(503),

        /**
         * 005订货单流水号
         */
        SOCIAL_COMMERCE_005_PURCHASE_SEQUENCE_NO(504),

        /**
         * 005订货单查询流水号
         */
        SOCIAL_COMMERCE_005_PURCHASE_QUERY_SEQUENCE_NO(505),

        /**
         * 004订货单
         */
        SOCIAL_COMMERCE_004_PURCHASE_DOC(801),

        /**
         * 004提货单
         */
        SOCIAL_COMMERCE_004_PICKUP_DOC(802),

        /**
         * 004出货单
         */
        SOCIAL_COMMERCE_004_SALES_DOC(803),

        /**
         * 004订货单流水号
         */
        SOCIAL_COMMERCE_004_PURCHASE_SEQUENCE_NO(804),

        /**
         * 004订货单查询流水号
         */
        SOCIAL_COMMERCE_004_PURCHASE_QUERY_SEQUENCE_NO(805),

        /**
         * 003订货单
         */
        SOCIAL_COMMERCE_003_PURCHASE_DOC(701),

        /**
         * 003提货单
         */
        SOCIAL_COMMERCE_003_PICKUP_DOC(702),

        /**
         * 003出货单
         */
        SOCIAL_COMMERCE_003_SALES_DOC(703),

        /**
         * 003订货单流水号
         */
        SOCIAL_COMMERCE_003_PURCHASE_SEQUENCE_NO(704),

        /**
         * 003订货单查询流水号
         */
        SOCIAL_COMMERCE_003_PURCHASE_QUERY_SEQUENCE_NO(705),
        /**
         * 003库存流水变动流水号
         */
        SOCIAL_COMMERCE_003_STOCK_CHANGE_SEQUENCE_NO(706),

        /**
         * 002订货单
         */
        SOCIAL_COMMERCE_002_PURCHASE_DOC(501),

        /**
         * 002提货单
         */
        SOCIAL_COMMERCE_002_PICKUP_DOC(502),

        /**
         * 002出货单
         */
        SOCIAL_COMMERCE_002_SALES_DOC(503),

        /**
         * 002订货单流水号
         */
        SOCIAL_COMMERCE_002_PURCHASE_SEQUENCE_NO(504),

        /**
         * 002订货单查询流水号
         */
        SOCIAL_COMMERCE_002_PURCHASE_QUERY_SEQUENCE_NO(505),

        /**
         * 001进货单
         */
        SOCIAL_COMMERCE_001_PURCHASE_DOC(601),

        /**
         * 001提货单
         */
        SOCIAL_COMMERCE_001_PICKUP_DOC(602),

        /**
         * 001出货单
         */
        SOCIAL_COMMERCE_001_SALES_DOC(603),

        /**
         * 001零售单
         */
        SOCIAL_COMMERCE_001_RETAIL_DOC(604),

        /**
         * 001进货单流水号
         */
        SOCIAL_COMMERCE_001_PURCHASE_SEQUENCE_NO(605),

        /**
         * 001进货单查询流水号
         */
        SOCIAL_COMMERCE_001_PURCHASE_QUERY_SEQUENCE_NO(606),
        /**
         * 001云仓出库单
         */
        SOCIAL_COMMERCE_001_OUT_STOCK_DOC(607),
        /**
         *
         */
        SOCIAL_COMMERCE_001_GIFT_BAG_DOC(608),
        /**
         * 拼团分组编号
         */
        SOCIAL_COMMERCE_001_ORDER_GROUP_DOC(609),
        /**
         * 拼团订单号
         */
        SOCIAL_COMMERCE_001_ORDER_GROUP_GOODS_DOC(610),
        /**
         * 库存变动流水编号
         */
        SOCIAL_COMMERCE_001_STOCK_CHANGE_RECORD_DOC(611),

        /**
         * 黑金促销礼包编号
         */
        SOCIAL_COMMERCE_001_PROMOTIONAL_GIFT_BAG_DOC(612),

        /************************ MONGO主键自增 ***********************/
        /**
         * Mongo自增统一前缀
         */
        MONGO_ID_INCRI(911);

        @EnumValue
        private final Integer key;

        DocTypeEnum(Integer key) {
            this.key = key;
        }
    }

}
