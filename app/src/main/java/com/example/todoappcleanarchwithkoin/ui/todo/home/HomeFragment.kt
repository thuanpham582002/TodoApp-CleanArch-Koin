package com.example.todoappcleanarchwithkoin.ui.todo.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.core.domain.util.TodoOrder
import com.example.todoappcleanarchwithkoin.MainActivity
import com.example.todoappcleanarchwithkoin.R
import com.example.todoappcleanarchwithkoin.databinding.FragmentHomeBinding
import com.example.todoappcleanarchwithkoin.ui.todo.home.components.adapter.TodoAdapter
import com.example.todoappcleanarchwithkoin.ui.todo.home.components.adapter.selectiontracker.TodosDetailsLookup
import com.example.todoappcleanarchwithkoin.ui.todo.home.components.adapter.selectiontracker.TodosKeyProvider
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), ActionMode.Callback, SearchView.OnQueryTextListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var todoAdapter: TodoAdapter
    private var actionMode: ActionMode? = null
    lateinit var selectionTracker: SelectionTracker<Long>
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupSpinnerGroup()
        setupRecyclerview()
        setupSelectionTrackerAdapter()
        onUIClick()
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.state.collect {
                    todoAdapter.setData(it.listTodo)
                    binding.rlOrder.visibility =
                        if (!it.isOrderSectionVisible) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun onUIClick() {
        binding.fabAdd.setOnClickListener {
            onDestroyActionMode(actionMode)
            homeViewModel.saveStateTodo(TodoEntity())
            it.findNavController().navigate(R.id.action_homeFragment_to_addEditFragment)
        }

        binding.btnOrder.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.ToggleOrderSection)
        }

        binding.rbDate.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.OrderBy.Date))
        }

        binding.rbTitle.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.OrderBy.Title))
        }

        binding.rbNone.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.OrderBy.None))
        }

        binding.rbAsc.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.OrderType.Ascending))
        }

        binding.rbDesc.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.OrderType.Descending))
        }

        binding.rbTodoAll.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.TodoType.All))
        }

        binding.rbTodoCompleted.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.TodoType.Completed))
        }

        binding.rbTodoUncompleted.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.TodoType.Uncompleted))
        }
    }

    private fun setupSelectionTrackerAdapter() {
        selectionTracker =
            SelectionTracker.Builder(
                "selectionItem",
                binding.recyclerView,
                TodosKeyProvider(todoAdapter),
                TodosDetailsLookup(binding.recyclerView),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()

        selectionTracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    if (actionMode == null) {
                        actionMode =
                            (requireActivity() as MainActivity).startSupportActionMode(this@HomeFragment)
                        Log.i("HomeFragment", "onSelectionChanged: ")
                    }

                    val items = selectionTracker.selection.size()
                    if (items > 0) {
                        actionMode?.title = "$items ${resources.getString(R.string.selected)}"
                    } else {
                        actionMode?.finish()
                    }
                }
            })
        todoAdapter.tracker = selectionTracker
    }

    private fun setupRecyclerview() {
        todoAdapter = TodoAdapter(homeViewModel)
        with(binding.recyclerView) {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSpinnerGroup() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.listGroup.collect {
                val group =
                    listOf(resources.getString(R.string.all)) + it.map { group -> group.name }
                spinner = binding.spinnerGroup
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, group)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = this@HomeFragment
                Log.i("HomeFragment", "setupSpinnerGroup: ")
            }
        }
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.toolbar.title = resources.getString(R.string.my_to_do_list)
            toolbar.toolbar.inflateMenu(R.menu.menu_main)
            setActionSearchToDo(toolbar.toolbar.menu)
            setOnMenuItemClickListener(toolbar.toolbar)
        }
    }

    private fun setOnMenuItemClickListener(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_search -> {
                    true
                }
                R.id.action_settings -> {
                    findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setActionSearchToDo(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        // set text color
        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        AppCompatDelegate.getDefaultNightMode().let { mode ->
            if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                editText.setHintTextColor(Color.GRAY)
                editText.setTextColor(Color.BLACK)
            } else {
                editText.setHintTextColor(Color.DKGRAY)
                editText.setTextColor(Color.WHITE)
            }
        }
        searchView.queryHint =
            resources.getString(R.string.search_to_do)
        searchView.setOnQueryTextListener(this@HomeFragment)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.i("onQueryTextChange", "onQueryTextChange: $newText")
        val listTodo = homeViewModel.state.value.listTodo.filter { todo ->
            todo.title.contains(newText.toString(), true)
        }
        todoAdapter.setData(listTodo)
        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        binding.fabAdd.hide()
        mode?.menuInflater?.inflate(R.menu.menu_delete, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete -> {
                val selected = todoAdapter.dataList.filter {
                    selectionTracker.selection.contains(it.id)
                }.toMutableList()

                selected.forEach {
                    homeViewModel.onEvent(HomeEvent.DeleteToDo(it))
                }
                actionMode?.finish()
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        Log.i("HomeFragment", "onDestroyActionMode: ")
        binding.fabAdd.show()
        selectionTracker.clearSelection()
        actionMode = null
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2 == 0)
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.GroupType.All))
        else
            homeViewModel.onEvent(HomeEvent.Order(TodoOrder.GroupType.Custom(spinner.selectedItem.toString())))
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}