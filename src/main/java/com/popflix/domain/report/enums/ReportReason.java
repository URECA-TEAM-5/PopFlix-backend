package com.popflix.domain.report.enums;

public enum ReportReason {
    ABUSE("욕설, 비방, 차별, 혐오"),
    PROMOTION("홍보, 영리목적"),
    ILLEGAL("불법 정보"),
    PORNOGRAPHIC("음란, 청소년 유해"),
    PRIVACY("개인 정보 노출, 유포, 거래"),
    SPAM("도배, 스팸"),
    RIGHTS("권리침해 신고");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
