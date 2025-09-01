package com.example.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

/**
 * Custom Lint Class
 * TODO : Detector 구현 및 List 등록 필요
 */
class LintIssueRegistry : IssueRegistry() {
    override val issues = listOf(
        DesignSystemDetector.ISSUE
    )

    override val api = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "OnDevice-AI",
        feedbackUrl = "",
        contact = "",
    )
}