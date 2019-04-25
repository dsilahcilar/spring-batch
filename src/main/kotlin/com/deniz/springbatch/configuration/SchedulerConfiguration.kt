package com.deniz.springbatch.configuration

import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.io.File

@Configuration
@EnableScheduling
@EnableBatchProcessing
class SchedulerConfiguration {

    @Autowired
    lateinit var jobLauncher: JobLauncher

    @Autowired
    lateinit var jobFactory: JobFactory

    @Value("\${importPath}")
    lateinit var importPath: String


    @Scheduled(fixedDelay = 30 * 1000)
    fun schedule() {
        println("New jobs are being created")

        for (file in getFiles()) {
            val jobParameters = JobParameters(mutableMapOf(
                    "time" to JobParameter(System.currentTimeMillis()),
                    "file" to JobParameter(file)
            ))
            val job = jobFactory.createJob(file)

            val jobExecution = jobLauncher.run(job, jobParameters)
            while (jobExecution.isRunning()) {
                System.out.println("...")
            }
        }
    }

    fun getFiles(): List<String> {
        return File(importPath).listFiles()
                .filter { file -> file.isFile }
                .map { file -> file.name }
                .filter { file -> file.endsWith(".csv") }
    }

}