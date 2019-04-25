package com.deniz.springbatch.configuration

import com.deniz.springbatch.model.Invoice
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths

@Component
class JobFactory {
    @Autowired
    val jobBuilderFactory: JobBuilderFactory? = null

    @Autowired
    val stepBuilderFactory: StepBuilderFactory? = null

    @Value("\${rootPath}")
    lateinit var rootPath: String

    @Value("\${importPath}")
    lateinit var importPath: String

    @Value("\${handledPath}")
    lateinit var handledPath: String


    fun createJob(fileName: String):Job {
        val step = invoiceHttpStep(fileName)
        return invoiceImportJob(step,fileName)
    }

    fun invoiceImportJob(invoiceHttpStep: Step, fileName: String): Job {
        return jobBuilderFactory!!.get("invoiceImport" + fileName + "job")
                .start(invoiceHttpStep)
                .next(moveHandledStep(fileName))
                .build()
    }

    fun invoiceHttpStep(fileName: String) : Step {
        return stepBuilderFactory!!.get("invoiceHttpStep")
                .chunk<Invoice, Invoice>(1)
                .reader(fileReader(fileName))
                //    .processor(processor())
                .writer(httpWriter())
                .build()
    }

    fun moveHandledStep(fileName: String): Step {
         val fileMovingTasklet = FileMovingTasklet(Paths.get("$importPath/$fileName"),handledPath)

        return stepBuilderFactory!!.get("moveHandledStep")
                .tasklet(fileMovingTasklet)
                .build()
    }

    private fun httpWriter(): ItemWriter<Invoice> {
        return ItemWriter { invoice -> println("I am calling another resource $invoice") }
    }

    fun fileReader(fileName:String): FlatFileItemReader<Invoice> {
        val reader = FlatFileItemReader<Invoice>()
        val path = "$importPath/$fileName"
        reader.setResource(FileSystemResource(path))
        //reader.setResource(ClassPathResource("invoice-1.csv"))
        reader.setLinesToSkip(1)

        val lineMapper = DefaultLineMapper<Invoice>()
        val tokenizer = DelimitedLineTokenizer()
        tokenizer.setNames("username", "userid","date","amount")

        val fieldSetMapper = BeanWrapperFieldSetMapper<Invoice>()
        fieldSetMapper.setTargetType(Invoice::class.java)

        lineMapper.setFieldSetMapper(fieldSetMapper)
        lineMapper.setLineTokenizer(tokenizer)
        reader.setLineMapper(lineMapper)

        return reader
    }

}