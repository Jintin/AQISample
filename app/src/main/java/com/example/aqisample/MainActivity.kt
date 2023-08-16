package com.example.aqisample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.carousell.monoadapter.MonoAdapter
import com.example.aqisample.databinding.ActivityMainBinding
import com.example.aqisample.databinding.AdapterAqiBinding
import com.example.aqisample.databinding.AdapterAqiCardBinding
import com.example.aqisample.utils.AqiDiffChecker
import com.example.aqisample.utils.repeatOnStart
import com.jintin.bindingextension.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cardAdapter = initCardAdapter()
        val detailAdapter = initDetailAdapter()
        with(binding.cardView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cardAdapter
        }
        with(binding.detailView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = detailAdapter
        }
        repeatOnStart {
            viewModel.filterInfo.collect { info ->
                with(binding.searchInfo) {
                    if (info.isEnable) {
                        if (info.filter.isEmpty()) {
                            text = "輸入[站名]\n查詢該地區空污資訊"
                            isVisible = true
                        } else if (info.isEmptyResult) {
                            text = "找不到[${info.filter}]\n相關的空污資訊"
                            isVisible = true
                        } else {
                            isVisible = false
                        }
                    } else {
                        isVisible = false
                    }
                }
            }
        }
        repeatOnStart {
            viewModel.aqiCardFlow.collect(cardAdapter::submitList)
        }
        repeatOnStart {
            viewModel.aqiDetailFlow.collect(detailAdapter::submitList)
        }
        repeatOnStart {
            viewModel.uiState.collect { uiState ->
                binding.progressBar.isVisible = uiState == MainViewModel.UiState.Loading
                binding.errorText.isVisible = uiState == MainViewModel.UiState.Fail
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.search)
        val refreshItem = menu.findItem(R.id.menu_refresh)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                viewModel.setSearchEnable(false)
                refreshItem.isVisible = true
                invalidateOptionsMenu()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                viewModel.setSearchEnable(true)
                refreshItem.isVisible = false
                invalidateOptionsMenu()
                return true
            }
        })
        if (viewModel.shouldOpenSearchView()) {
            searchItem.expandActionView()
        } else {
            searchItem.collapseActionView()
        }

        with(searchItem.actionView as SearchView) {
            isSubmitButtonEnabled = false
            setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        viewModel.setFilterString(query.orEmpty())
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.setFilterString(newText.orEmpty())
                        return true
                    }
                })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> viewModel.refresh()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initCardAdapter() =
        MonoAdapter.create(AdapterAqiCardBinding::inflate, AqiDiffChecker) {
            siteId.text = it.siteId.toString()
            siteName.text = it.siteName
            pm25.text = it.pm25.toString()
            county.text = it.county
            status.text = it.status
        }

    private fun initDetailAdapter() =
        MonoAdapter.create(AdapterAqiBinding::inflate, AqiDiffChecker) { data ->
            siteId.text = data.siteId.toString()
            siteName.text = data.siteName
            pm25.text = data.pm25.toString()
            county.text = data.county
            status.text = if (data.isGoodWeather()) {
                "The status is good, we want to go out to have fun."
            } else {
                data.status
            }
            this.root.setOnClickListener {
                if (!data.isGoodWeather()) {
                    Toast.makeText(it.context, "What can I do for you?", Toast.LENGTH_SHORT).show()
                }
            }
            arrow.isVisible = !data.isGoodWeather()
        }
}
