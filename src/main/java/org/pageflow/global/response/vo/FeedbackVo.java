package org.pageflow.global.response.vo;

import org.pageflow.global.business.BizConstraint;

/**
 * @author : sechan
 */
public record FeedbackVo(BizConstraint bizConstraint, String message) {
}
