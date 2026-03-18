package com.technokratos.submissionserviceimpl.entity;

public interface ProblemVisibleIOAndLimits {
    String getVisibleInputs();
    String getVisibleOutputs();
    Integer getVisibleCpuTimeLimit();
    Integer getVisibleMemoryLimit();
}
