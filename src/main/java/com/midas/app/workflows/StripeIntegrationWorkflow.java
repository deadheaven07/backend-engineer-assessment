package com.midas.app.workflows;

import io.temporal.workflow.WorkflowMethod;

public class StripeIntegrationWorkflow {
  @WorkflowMethod
  void createStripeCustomer(String email) {}
}
