<template>
  <div class="search-area">
    <input
      type="text"
      v-model="searchQuery"
      placeholder="请输入您要搜索的内容..."
      @keyup.enter="performSearch"
    />
    <button class="search-button" @click="performSearch">
      🔍搜索
    </button>
    <div v-if="searchResults && searchResults.length > 0" class="search-results">
      <div v-for="result in paginatedResults" :key="result.name" class="search-result" @click="showInfo(result)">
        <div class="result-info">
          <div class="result-image">
            <img :src="result.imageUrl" alt="Entity Image" class="entity-image">
          </div>
          <div class="result-details">
            <h3>{{ result.name }}</h3>
            <p>{{ result.shortDescription }}</p>
          </div>
        </div>
      </div>
    </div>
    <div v-if="searchResults && searchResults.length > 0" class="fixed-pagination">
      <button :disabled="currentPage === 1" @click="prevPage">上一页</button>
      <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
      <button :disabled="currentPage === totalPages" @click="nextPage">下一页</button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    onShowInfo: Function,
  },
  data() {
    return {
      searchQuery: '',
      currentPage: 1,
      itemsPerPage: 5, // 每页显示的条数
      totalPages: 1, // 总页数
      searchResults: [], // 搜索结果
    };
  },
  computed: {
    paginatedResults() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.searchResults.slice(start, end);
    },
  },
  methods: {
    calculateTotalPages() {
      this.totalPages = Math.ceil(this.searchResults.length / this.itemsPerPage);
    },
    async performSearch() {
      this.searchResults = [];
      const query = this.searchQuery.trim().toLowerCase();
      if (query) {
        this.currentPage = 1;
        try {
          await this.fetchPaginatedResults(this.currentPage, this.itemsPerPage, query);
        } catch (error) {
          console.error('执行搜索时发生错误:', error);
        }
      }
    },
    async fetchPaginatedResults(page, pageSize, name = '') {
      try {
        const queryParams = new URLSearchParams({
          page: page, 
          pageSize: pageSize,
          status: 1, 
          name: name,
        });

        const response = await fetch(`http://8.155.5.178:8080/api/entity/pages?${queryParams}`, {
          method: 'GET',
        });

        const data = await response.json();

        if (response.ok && data.code === 1) {
          if (page === 1) {
            // 第一次请求时，重置 searchResults
            this.searchResults = data.data.record.map(result => ({
              id: result.id,
              name: result.name,
              shortDescription: result.overview,
              imageUrl: result.imageUrl || 'default-image-url.jpg'
            }));
            this.totalPages = Math.ceil(data.data.total / pageSize); // 计算总页数
          } else {
            // 后续请求时，追加数据到 searchResults
            this.searchResults = [
              ...this.searchResults,
              ...data.data.record.map(result => ({
                id: result.id,
                name: result.name,
                shortDescription: result.overview,
                imageUrl: result.imageUrl || 'default-image-url.jpg' 
              }))
            ];
          }
        } else {
          this.searchResults = [];
          this.totalPages = 1;
          console.error('数据获取失败，错误码：', data.code);
        }
      } catch (error) {
        console.error('请求分页数据时发生错误:', error);
        this.searchResults = [];
        this.totalPages = 1;
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.fetchPaginatedResults(this.currentPage, this.itemsPerPage, this.searchQuery)
          .then(() => {
            console.log('当前页:', this.currentPage);
          })
          .catch(error => {
            console.error('获取分页数据时发生错误:', error);
          });
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.fetchPaginatedResults(this.currentPage, this.itemsPerPage, this.searchQuery)
          .then(() => {
            console.log('当前页:', this.currentPage);
          })
          .catch(error => {
            console.error('获取分页数据时发生错误:', error);
          });
      }
    },
    showInfo(node) {
      this.onShowInfo(node);
    },
  },
};
</script>

<style scoped>
.search-area {
  margin-top: 20px;
}

.search-area input {
  width: 100%;
  max-width: 300px;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 4px 0 0 4px;
}

.search-button {
  padding: 5px 10px;
  border: none;
  background-image: linear-gradient(#45496a,#7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 0 4px 4px 0;
}

.search-button:hover {
  background-image: linear-gradient(#a1a3b4,#b1a9b9);
}

.search-result {
  margin-bottom: 10px;
  padding: 10px;
  cursor: pointer;
  display: flex;
  align-items: center; /* 上下居中 */
}

.search-result:hover {
  background-color: #f4f4f4;
}

.result-info {
  display: flex;
  align-items: center; /* 上下居中 */
  width: 100%;
}

.result-image {
  margin-right: 10px;
  text-align: left; /* 左对齐 */
}

.entity-image {
  max-width: 100px;
  height: auto;
}

.result-details {
  flex-grow: 1;
  text-align: left; /* 左对齐 */
}

.result-details h3 {
  margin: 0;
  margin-bottom: 5px;
}

.result-details p {
  margin: 0;
}

.fixed-pagination {
  position: fixed;
  bottom: 0;
  right: 500px;
  background-color: white;
  padding: 20px;
}
</style>