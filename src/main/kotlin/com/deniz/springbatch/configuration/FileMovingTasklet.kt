package com.deniz.springbatch.configuration

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileMovingTasklet(private val sourcePath: Path, private val destPath: String) : Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val time = chunkContext.stepContext.jobParameters["time"]
        val file = chunkContext.stepContext.jobParameters["file"]
        chunkContext.stepContext.stepExecutionContext
        Files.move(sourcePath, Paths.get("$destPath/$time-$file"))
        return RepeatStatus.FINISHED
    }

}