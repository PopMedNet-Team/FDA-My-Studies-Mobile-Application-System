package com.harvard.studyAppModule.custom.Result;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.database.TaskRecord;

import java.util.Date;
import java.util.List;

/**
 * Created by Naveen Raj on 02/15/2017.
 */
public class TaskRecordCustom {
    public int id;
    public String taskId;
    public Date started;
    public Date completed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    public Date getUploaded() {
        return uploaded;
    }

    public void setUploaded(Date uploaded) {
        this.uploaded = uploaded;
    }

    public Date uploaded;

    public static TaskResult toTaskResult(TaskRecord taskRecord, List<StepRecordCustom> stepRecords)
    {
        TaskResult taskResult = new TaskResult(taskRecord.taskId);
        taskResult.setStartDate(taskRecord.started);
        taskResult.setEndDate(taskRecord.completed);

        for(StepRecordCustom record : stepRecords)
        {
            StepResult result = StepRecordCustom.toStepResult(record);
            taskResult.setStepResultForStepIdentifier(result.getIdentifier(), result);
        }
        return taskResult;
    }
}
