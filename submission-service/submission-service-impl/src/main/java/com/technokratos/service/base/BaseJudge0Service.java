package com.technokratos.service.base;

import com.technokratos.dto.request.RunRequest;
import com.technokratos.enums.Action;

public interface BaseJudge0Service {
    void sendBatchSubmission(RunRequest request, Action action);
    void sendSubmission(RunRequest request, Action action);
}

