package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roadmaintenance.R
import com.example.roadmaintenance.adapter.PathListAdapter
import com.example.roadmaintenance.viewmodels.RequestManager
import com.example.roadmaintenance.databinding.FragmentHomeBinding
import com.example.roadmaintenance.fileManager.FileCache
import com.example.roadmaintenance.models.Pathway

class HomeFragment : Fragment() {


    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var pathList: List<Pathway>? = null
    private lateinit var recyclerView: RecyclerView
    private var pathListAdapter: PathListAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val fileCache: FileCache by lazy {
        FileCache(requireContext())
    }
    private val requestManager: RequestManager by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configPathListRecyclerView()

        configSwipeToRefresh()

        configRequestsObservers()

    }


    private fun configSwipeToRefresh() {
        swipeRefresh = binding.swipeRefresh
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.primary),
            ContextCompat.getColor(requireContext(), R.color.primary_dark)
        )
        swipeRefresh.setOnRefreshListener {
            updateData()
        }

    }

    private fun configRequestsObservers() {
        requestManager.fetchResponse.observe(requireActivity()) {
            Log.i("Fetch home fragment", it?.body()?.size.toString())
            pathList = it?.body()
            pathListAdapter?.setPathList(pathList?.toMutableList())
        }
        requestManager.sendResponse.observe(requireActivity()) {
            it.let {
                if (it.isSuccessful)
                    updateData()
            }
        }
    }

    private fun configPathListRecyclerView() {
        recyclerView = binding.recyclerView

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        pathListAdapter = PathListAdapter(pathList?.toMutableList())
        recyclerView = binding.recyclerView

        recyclerView?.run {
            layoutManager = linearLayoutManager
            adapter = pathListAdapter
        }
    }

    private fun updateData() {
        requestManager.fetchData()
        swipeRefresh.isRefreshing = false
        pathListAdapter?.setPathList(pathList?.toMutableList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

