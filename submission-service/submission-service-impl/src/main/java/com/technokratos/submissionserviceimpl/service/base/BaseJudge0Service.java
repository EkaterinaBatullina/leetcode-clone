package com.technokratos.submissionserviceimpl.service.base;

import com.technokratos.problemserviceapi.dto.request.RunRequest;
import com.technokratos.submissionserviceapi.enums.Action;

public interface BaseJudge0Service {
<<<<<<< HEAD
    void sendSubmission(RunRequest request, Action action);
}
=======
    void sendBatchSubmission(RunRequest request, Action action);
    void sendSubmission(RunRequest request, Action action);
}

>>>>>>> feature/problem-and-submission-service
