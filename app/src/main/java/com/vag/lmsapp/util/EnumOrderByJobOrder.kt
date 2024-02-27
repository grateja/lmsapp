package com.vag.lmsapp.util

enum class EnumOrderByJobOrder(val itemIndex: Int, val description: String, val key: String) {
    CUSTOMER_NAME(0, "Customer Name", "customer_name"),
    JOB_ORDER_NUMBER(1, "Job Order Number", "job_order_number"),
    DATE_CREATED(2, "Date Created", "created_at"),
    DATE_PAID(3, "Date Paid", "date_paid"),
}