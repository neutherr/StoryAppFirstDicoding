    package com.dicoding.picodiploma.nganugramstoryapp.data

    import android.util.Log
    import androidx.paging.PagingSource
    import androidx.paging.PagingState
    import com.dicoding.picodiploma.nganugramstoryapp.data.response.ListStoryItem
    import com.dicoding.picodiploma.nganugramstoryapp.repository.StoryRepository


    class PagingStoriesSource(private val storiesRepository: StoryRepository) : PagingSource<Int, ListStoryItem>() {

        companion object {
            private const val PAGE_INDEX = 1
        }

        override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
            return try {
                val page = params.key ?: PAGE_INDEX
                Log.d("PagingStoriesSource", "Loading page $page with size ${params.loadSize}")
                val responseData = storiesRepository.getListPaging(
                    location = null,
                    page = page,
                    size = params.loadSize
                )
                Log.d("PagingStoriesSource", "Data loaded: ${responseData.size} items")
                LoadResult.Page(
                    data = responseData,
                    prevKey = if (page == PAGE_INDEX) null else page - 1,
                    nextKey = if (responseData.isEmpty()) null else page + 1
                )
            } catch (exception: Exception) {
                Log.e("PagingStoriesSource", "Error loading data", exception)
                LoadResult.Error(exception)
            }
        }
    }

