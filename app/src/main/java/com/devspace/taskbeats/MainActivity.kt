package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Variáveis globais - Posso usá-las em todo o escopo do projeto

    private var categories = listOf<CategoryUiData>()
    private var tasks = listOf<TaskUiData>()

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskBeatDataBase::class.java, "database-task-beat"
        ).build()
    }

    private val categoryDAO by lazy {
        db.getCategoryDao()
    }

    private val taskDao: TaskDao by lazy {
        db.getTaskDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()
        val categoryAdapter = CategoryListAdapter()

        categoryAdapter.setOnClickListener { selected ->
            if(selected.name == "+"){
                val createCategoryBottomSheet = CreateCategoryBottomSheet()
                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")
            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }

                val taskTemp =
                    if (selected.name != "ALL") {
                        tasks.filter { it.category == selected.name }
                    } else {
                        tasks
                    }

                taskAdapter.submitList(taskTemp)

                categoryAdapter.submitList(categoryTemp)

            }

            rvCategory.adapter = categoryAdapter
            getCategoriesFromDataBase(categoryAdapter)

            rvTask.adapter = taskAdapter
            getTasksFromDataBase(taskAdapter)
        }
    }


    private fun getCategoriesFromDataBase(adapter: CategoryListAdapter){
        GlobalScope.launch(Dispatchers.IO){
            val categoriesFromDB: List<CategoryEntity> = categoryDAO.getAll()
            val categoriesUiData = categoriesFromDB.map {
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )
            } // Adicionando uma categoria "fake" com um botão de plus
                .toMutableList()

            categoriesUiData.add(
                CategoryUiData(
                    name = "+",
                    isSelected = false
                )
            )

            GlobalScope.launch(Dispatchers.Main){
                categories = categoriesUiData
                adapter.submitList(categoriesUiData)
            }
        }
    }

    private fun getTasksFromDataBase(adapter: TaskListAdapter){
        GlobalScope.launch(Dispatchers.IO){
            val tasksFromDb: List<TaskEntity> = taskDao.getAll()
            val tasksUiData = tasksFromDb.map {
                TaskUiData(
                    name = it.name,
                    category = it.category
                )
            }
            GlobalScope.launch(Dispatchers.Main){
                tasks = tasksUiData
                adapter.submitList(tasksUiData)
            }
        }
    }
}