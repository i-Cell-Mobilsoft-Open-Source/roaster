package hu.icellmobilsoft.roaster.testsuite.jaxb.dto;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseRequest;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

/**
 * Dto helper class
 * 
 * @author imre.scheffer
 * @since 0.8.0
 */
public class DtoHelper {

    /**
     * Currently not CDI bean
     */
    private DtoHelper() {
    }

    /**
     * Create filled {@code BaseRequest}
     * 
     * @return new filled {@code BaseRequest}
     */
    public static BaseRequest createBaseRequest() {
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.withContext(createContextType());
        return baseRequest;
    }

    /**
     * Create filled {@code ContextType}
     * 
     * @return new filled {@code ContextType}
     */
    public static ContextType createContextType() {
        return new ContextType().withRequestId(RandomUtil.generateId()).withTimestamp(DateUtil.nowUTC());
    }
}
