<template>
  <div class="my-knowledge">
    <!-- 按钮部分 -->
    <div class="header">
      <div class="tabs">
        <button :class="{ active: activeTab === 'uploaded' }" @click="setActiveTab('uploaded')">已上传</button>
        <button :class="{ active: activeTab === 'pending' }" @click="setActiveTab('pending')">待审核</button>
        <button :class="{ active: activeTab === 'rejected' }" @click="setActiveTab('rejected')">已驳回</button>
      </div>
    </div>
    <!-- 列表部分 -->
    <div class="knowledge-list">
      <table>
        <thead>
          <tr>
            <th class="col-name">名称</th>
            <th class="col-overview">简述</th>
            <th class="col-created">创建时间</th>
            <th v-if="activeTab !== 'rejected'" class="col-updated">更新时间</th>
            <th v-else class="col-rejected">驳回原因</th>
            <th class="col-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td class="col-name">{{ item.name }}</td>
            <td class="col-overview">{{ item.overview }}</td>
            <td class="col-created">{{ item.createdAt }}</td>
            <td v-if="activeTab !== 'rejected'" class="col-updated">{{ item.updatedAt }}</td>
            <td v-else class="col-rejected">{{ item.rejectReason }}</td>
            <td class="col-action"><button @click="openModal(item)">查看</button></td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 模态窗口 -->
    <div v-if="isModalVisible" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h2>{{ selectedItem.name }}</h2>
        <img :src="selectedItem.imageUrl" alt="Entity Image" class="modal-image">
        <p><strong class="left-align">简述:</strong></p>
        <p class="left-align">{{ selectedItem.overview }}</p>
        <p><strong class="left-align">特点:</strong></p>
        <p class="left-align">{{ selectedItem.feature }}</p>
        <p><strong class="left-align">详情描述:</strong></p>
        <p class="left-align">{{ selectedItem.description }}</p>
        <p v-if="selectedItem.rejectReason" class="left-align"><strong>驳回原因:</strong></p>
        <p v-if="selectedItem.rejectReason" class="left-align">{{ selectedItem.rejectReason }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from '../http';

export default {
  data() {
    return {
      activeTab: 'uploaded',
      allItems: [], // 存储所有数据
      items: [], // 存储当前显示的数据
      infoPanelVisible: false,
      isModalVisible: false,
      selectedItem: {},
    };
  },
  methods: {
    setActiveTab(tab) {
      this.activeTab = tab;
      this.filterItems();
    },
    async fetchResults() {
      try {
        const userId = sessionStorage.getItem('userId');
        const response = await axios.get(`/api/entity/get_info/${userId}`);
        const data = response.data;

        if (response.status === 200 && data.code === 1) {
          this.allItems = data.data;
          this.filterItems(); // 初始化时过滤数据
        } else {
          this.allItems = [];
          this.items = [];
        }
      } catch (error) {
        this.allItems = [];
        this.items = [];
      }
    },
    filterItems() {
      const status = this.getStatusForTab(this.activeTab);
      this.items = this.allItems.filter(item => item.status === status);
    },
    getStatusForTab(tab) {
      switch (tab) {
        case 'uploaded':
          return 1;
        case 'pending':
          return 0;
        case 'rejected':
          return -1;
        default:
          return 1;
      }
    },
    openModal(item) {
      this.selectedItem = item;
      this.isModalVisible = true;
    },
    closeModal() {
      this.isModalVisible = false;
    },
  },
  mounted() {
    this.fetchResults();
  }
};
</script>


<style scoped>
.my-knowledge {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  padding-top: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-left: 50px;
}

.tabs {
  display: flex;
  justify-content: flex-start;
}

.tabs button {
  padding: 10px 20px;
  border: none;
  background-image: linear-gradient(#45496a,#7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 4px;
  margin-right: 20px;
}

.tabs button:hover { 
  background-image: linear-gradient(#a1a3b4,#b1a9b9); 
}

.tabs button.active {
  background-color: #333a65;
  background-image: none; 
}

.knowledge-list {
  flex: 1;
  width: 100%;
}

.knowledge-list table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.knowledge-list th, .knowledge-list td {
  padding: 10px;
  text-align: center;
  border: 1px solid #ccc;
  overflow: hidden;
  text-overflow: ellipsis;
}

.knowledge-list th {
  background-color: #f0f0f0;
}

.knowledge-list td button {
  padding: 5px 10px;
  background-image: linear-gradient(#45496a,#7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 4px;
  border: none;
}

.knowledge-list td button:hover {
  background-image: linear-gradient(#a1a3b4,#b1a9b9);
}

.knowledge-list th.col-name,
.knowledge-list td.col-name {
  width: 10%;
}

.knowledge-list th.col-overview,
.knowledge-list td.col-overview {
  width: 30%;
  white-space: nowrap;
  overflow: hidden;    
  text-overflow: ellipsis; 
}

.knowledge-list th.col-created,
.knowledge-list td.col-created {
  width: 15%;
}

.knowledge-list th.col-updated,
.knowledge-list td.col-updated {
  width: 15%;
}

.knowledge-list th.col-action,
.knowledge-list td.col-action {
  width: 10%;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 600px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: relative;
}

.close {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
  font-size: 20px;
  color: rgb(168, 163, 163); 
  border: none;
  padding: 5px;
}

.modal-content img {
  max-width: 200px; 
  height: auto;
  margin-bottom: 10px;
}

.left-align {
  text-align: left;
  white-space: pre-wrap;
}

.modal-content strong {
  display: block; 
  margin-bottom: 5px; 
}
</style>