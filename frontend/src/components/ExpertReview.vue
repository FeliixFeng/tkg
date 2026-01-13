<template>
  <div class="expert-review">
    <!-- 按钮部分 -->
    <div class="header">
      <div class="tabs">
        <button :class="{ active: activeTab === 'unchecked' }" @click="setActiveTab('unchecked')">未评审</button>
        <button :class="{ active: activeTab === 'checked' }" @click="setActiveTab('checked')">已评审</button>
      </div>
      <div class="pagination">
        <button :disabled="activeTab === 'unchecked' && uncheckedCurrentPage === 1" @click="prevPage">上一页</button>
        <span>第 {{ activeTab === 'unchecked' ? uncheckedCurrentPage : checkedCurrentPage }} 页 / 共 {{ activeTab === 'unchecked' ? uncheckedTotalPages : checkedTotalPages }} 页</span>
        <button :disabled="activeTab === 'unchecked' && uncheckedCurrentPage === uncheckedTotalPages || activeTab === 'checked' && checkedCurrentPage === checkedTotalPages" @click="nextPage">下一页</button>
      </div>
    </div>
    <!-- 列表部分 -->
    <div class="review-list">
      <div v-if="activeTab === 'unchecked'">
        <table>
          <thead>
            <tr>
              <th class="col-name">名称</th>
              <th class="col-overview">简述</th>
              <th class="col-uploader">上传用户</th>
              <th class="col-action">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in uncheckedItems" :key="item.id">
              <td class="col-name">{{ item.name }}</td>
              <td class="col-overview">{{ item.overview }}</td>
              <td class="col-uploader">{{ item.username }}</td>
              <td class="col-action"><button @click="reviewItem(item)">评审</button></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="activeTab === 'checked'">
        <table>
          <thead>
            <tr>
              <th class="col-name">名称</th>
              <th class="col-overview">简述</th>
              <th class="col-uploader">上传用户</th>
              <th class="col-action">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in checkedItems" :key="item.id">
              <td class="col-name">{{ item.name }}</td>
              <td class="col-overview">{{ item.overview }}</td>
              <td class="col-uploader">{{ item.username }}</td>
              <td class="col-action"><button @click="reviewItem(item)">查看</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 模态窗口 -->
    <div v-if="isModalVisible" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h2>{{ selectedItem.name }}</h2>
        <img :src="selectedItem.imageUrl" alt="Entity Image" class="modal-image">
        <p><strong class="left-align">父节点:</strong></p>
        <p class="left-align description-text">{{ selectedItem.parentId }}</p>
        <p><strong class="left-align">简述:</strong></p>
        <p class="left-align description-text">{{ selectedItem.overview }}</p>
        <p><strong class="left-align">详情描述:</strong></p>
        <p class="left-align description-text">{{ selectedItem.description }}</p>
        <p><strong class="left-align">特点:</strong></p>
        <p class="left-align description-text">{{ selectedItem.feature }}</p>
        <p><strong class="left-align">上传用户:</strong></p>
        <p class="left-align description-text">{{ selectedItem.username }}</p>
        <!-- 动态显示按钮 -->
        <div class="modal-actions">
          <template v-if="activeTab === 'unchecked'">
            <button @click="approveItem">通过</button>
            <button @click="rejectItem">驳回</button>
          </template>
          <template v-if="activeTab === 'checked'">
            <button @click="editItem">修改</button>
            <button @click="deleteItem">删除</button>
          </template>
        </div>

        <!-- 驳回原因输入框 -->
        <div v-if="showRejectReasonInput">
          <label for="rejectReason">驳回原因:</label>
          <textarea id="rejectReason" v-model="rejectReason"></textarea>
          <button @click="submitRejectReason">确认驳回</button>
          <button @click="showRejectReasonInput = false">取消</button>
        </div>
      </div>
    </div>

    <!-- 编辑模态窗口 -->
    <div v-if="isEditModalVisible" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeEditModal">&times;</span>
        <h2>{{ selectedItem.name }}</h2>
        <div>
          <label for="parentName">父节点名称:</label>
          <input type="text" id="parentName" v-model="editForm.parentName" @blur="fetchParentId">
        </div>
        <div>
          <label for="overview">简述:</label>
          <textarea id="overview" v-model="editForm.overview"></textarea>
        </div>
        <div>
          <label for="description">详情描述:</label>
          <textarea id="description" v-model="editForm.description"></textarea>
        </div>
        <div>
          <label for="feature">特点:</label>
          <textarea id="feature" v-model="editForm.feature"></textarea>
        </div>
        <div>
          <label for="imageUrl">上传图片:</label>
          <input type="file" id="imageUrl" @change="handleFileUpload" accept="image/*">
        </div>
        <div class="modal-actions">
          <button @click="submitEdit">提交</button>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script>
import axios from '../http';

export default {
  data() {
    return {
      activeTab: 'unchecked',
      uncheckedItems: [],
      uncheckedCurrentPage: 1,
      uncheckedTotalPages: 1,
      checkedItems: [],
      checkedCurrentPage: 1,
      checkedTotalPages: 1,
      isModalVisible: false,
      selectedItem: {},
      parentName: '',
      pageSize: 15,
      isEditModalVisible: false,
      editForm: {}, 
      rejectReason: '', 
      showRejectReasonInput: false,
    };
  },
  methods: {
    setActiveTab(tab) {
      this.activeTab = tab;
      if (tab === 'unchecked') {
        this.fetchUncheckedPaginatedResults(this.uncheckedCurrentPage, this.pageSize);
      } else {
        this.fetchPaginatedResults(this.checkedCurrentPage, this.pageSize); 
      }
    },
    async fetchUncheckedPaginatedResults(page, pageSize, name = '') {
      try {
        const queryParams = new URLSearchParams({
          page: page,
          pageSize: pageSize,
          status: 0,
          name: name
        });
        const response = await axios.get(`/api/entity/pages?${queryParams}`);
        const data = response.data;
        if (data.code === 1) {
          this.uncheckedItems = data.data.record;
        }
      } catch (error) {
        console.error('Error fetching paginated results:', error);
      }
    },
    async fetchPaginatedResults(page, pageSize, name = '') {
      try {
        const queryParams = new URLSearchParams({
          page: page,
          pageSize: pageSize,
          status: 1,
          name: name
        });
        const response = await axios.get(`/api/entity/pages?${queryParams}`);
        const data = response.data;

        if (data.code === 1) {
          const items = data.data.record;
          for (const item of items) {
            item.username = await this.fetchUserName(item.userId);
          }
          this.checkedItems = items;
          this.checkedTotalPages = Math.ceil(data.data.total / pageSize);
        } else {
          this.checkedItems = [];
          this.checkedTotalPages = 1;
        }
      } catch (error) {
        this.checkedItems = [];
        this.checkedTotalPages = 1;
      }
    },
    async fetchUserName(userId) {
      try {
        const response = await axios.get(`/api/user/get_name/${userId}`);
        const data = await response.json();

        if (response.ok && data.code === 1) {
          return data.data;
        } else {
          console.error('Failed to fetch user name:', data);
          return null;
        }
      } catch (error) {
        console.error('Error fetching user name:', error);
        return null;
      }
    },
    reviewItem(item) {
      this.fetchEntityData(item.id);
    },
    async fetchEntityData(id) {
      try {
        const response = await axios.get(`/api/entity/${id}`);
        const data = await response.json();

        if (response.ok && data.code === 1) {
          const userName = await this.fetchUserName(data.data.userId);
          let parentNodeName = "这是一个根节点";
          
          if (data.data.parentId) {
            const parentResponse = await axios.get(`/api/entity/${data.data.parentId}`);
            const parentData = await parentResponse.json();
            
            if (parentResponse.ok && parentData.code === 1) {
              parentNodeName = parentData.data.name;
            } else {
              console.error('Failed to fetch parent node name:', parentData);
            }
          }

          this.selectedItem = {
            id: data.data.id,
            name: data.data.name,
            imageUrl: data.data.imageUrl,
            parentId: parentNodeName,
            overview: data.data.overview,
            description: data.data.description,
            feature: data.data.feature,
            username: userName,
            status: data.data.status,
            userId: data.data.userId
          };
          this.isModalVisible = true;
        } else {
          alert('获取数据失败');
        }
      } catch (error) {
        console.error('Error fetching entity data:', error);
        alert('获取数据失败');
      }
    },
    closeModal() {
      this.isModalVisible = false;
    },
    prevPage() {
      if (this.activeTab === 'unchecked' && this.uncheckedCurrentPage > 1) {
        this.uncheckedCurrentPage--;
        this.fetchUncheckedPaginatedResults(this.uncheckedCurrentPage, this.pageSize); 
      } else if (this.activeTab === 'checked' && this.checkedCurrentPage > 1) {
        this.checkedCurrentPage--;
        this.fetchPaginatedResults(this.checkedCurrentPage, this.pageSize); 
      }
    },
    nextPage() {
      if (this.activeTab === 'unchecked' && this.uncheckedCurrentPage < this.uncheckedTotalPages) {
        this.uncheckedCurrentPage++;
        this.fetchUncheckedPaginatedResults(this.uncheckedCurrentPage, this.pageSize); 
      } else if (this.activeTab === 'checked' && this.checkedCurrentPage < this.checkedTotalPages) {
        this.checkedCurrentPage++;
        this.fetchPaginatedResults(this.checkedCurrentPage, this.pageSize);
      }
    },
    async approveItem() {
      try {
        const response = await axios.put(`/api/entity/check_ok/${this.selectedItem.id}`);
        if (response.data.code === 1) {
          alert('审批成功');
        }
      } catch (error) {
        console.error('Error approving item:', error);
      } finally {
        this.closeModal();
      }
    },

    rejectItem() {
      this.showRejectReasonInput = true;
    },
    submitRejectReason() {
      if (!this.rejectReason) {
        alert('请提供驳回原因');
        return;
      }

      if (typeof this.rejectReason !== 'string') {
        alert('驳回原因必须是字符串');
        return;
      }

      this.rejectItemWithReason(this.rejectReason); 
      this.showRejectReasonInput = false;
    },
    async rejectItemWithReason(reason) {
      try {
        console.log('Rejecting item with ID:', this.selectedItem.id);
        console.log('Reject reason:', reason);

        const requestBody = {
          id: this.selectedItem.id,
          rejectReason: reason
        };

        console.log('Request body:', requestBody);

        const response = await fetch(`http://8.155.5.178:8080/api/entity/check_no`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(requestBody)
        });

        const data = await response.json();

        console.log('Response from server:', data);

        if (response.ok && data.code === 1) {
          console.log('Item rejected successfully:', this.selectedItem);
          alert('驳回成功');
          this.fetchUncheckedPaginatedResults(this.uncheckedCurrentPage, this.pageSize);
        } else {
          console.error('Failed to reject item:', data);
          alert('驳回失败');
        }
      } catch (error) {
        console.error('Error rejecting item:', error);
        alert('驳回失败');
      } finally {
        this.closeModal();
      }
    },

    editItem() {
      this.isEditModalVisible = true;

      // 初始化编辑表单
      this.editForm = {
        parentName: this.selectedItem.parentName || '', 
        description: this.selectedItem.description || '', 
        overview: this.selectedItem.overview || '',
        feature: this.selectedItem.feature || '', 
        imageUrl: this.selectedItem.imageUrl || '' 
      };
    },

    closeEditModal() {
      this.isEditModalVisible = false;
    },

    async handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        const formData = new FormData();
        formData.append('file', file);

        try {
          const response = await axios.post('/api/upload', formData);

          const data = await response.json();

          if (response.ok && data.code === 1) {
            this.editForm.imageUrl = data.data; 
            console.log('File uploaded successfully:', data);
            alert('文件上传成功');
          } else {
            console.error('Failed to upload file:', data);
            alert('文件上传失败');
          }
        } catch (error) {
          console.error('Error uploading file:', error);
          alert('文件上传失败');
        }
      }
    },

    // 提交修改
    async submitEdit() {
      try {
        // 如果父节点名称被修改，获取新的 parentId
        if (this.editForm.parentName && this.editForm.parentName !== this.selectedItem.parentName) {
          await this.fetchParentId(); 
        }

         // 确保 parentId 是一个数字类型
     const parentId = typeof this.selectedItem.parentId === 'number' ? this.selectedItem.parentId : 0;

        // 构造更新数据
        const updatedItem = {
          id: this.selectedItem.id,
          name: this.selectedItem.name, 
          parentId: parentId, 
          description: this.editForm.description || this.selectedItem.description, 
          overview: this.editForm.overview || this.selectedItem.overview, 
          feature: this.editForm.feature || this.selectedItem.feature,
          status: this.selectedItem.status, 
          userId: this.selectedItem.userId, 
          imageUrl: this.editForm.imageUrl || this.selectedItem.imageUrl 
        };

        console.log('Submitting updated item:', updatedItem); // 调试信息

        const response = await axios.post('/api/entity', updatedItem);

        const data = await response.json();

        if (response.ok && data.code === 1) {
          console.log('Item edited successfully:', updatedItem);
          alert('修改成功');
          this.fetchPaginatedResults(this.checkedCurrentPage, this.pageSize); 
        } else {
          console.error('Failed to edit item:', data);
          alert('修改失败');
        }
      } catch (error) {
        console.error('Error editing item:', error);
        alert('修改失败');
      } finally {
        this.closeEditModal();
      }
    },

    // 获取父节点ID
    async fetchParentId() {
      if (this.editForm.parentName) {
        try {
          const response = await fetch(
            `http://8.155.5.178:8080/api/entity/get_id/${encodeURIComponent(this.editForm.parentName)}`
          );

          const data = await response.json();

          if (response.ok && data.code === 1 && data.data !== null) {
            this.selectedItem.parentId = data.data;
          } else {
            console.error('Failed to get parentId:', data);
            alert('获取父节点ID失败');
            this.selectedItem.parentId = '';
          }
        } catch (error) {
          console.error('Error getting parentId:', error);
          alert('获取父节点ID失败');
          this.selectedItem.parentId = '';
        }
      } else {
        this.selectedItem.parentId = '';
      }
    },

    async deleteItem() {
      try {
        console.log('Approving item with details:', this.selectedItem);

        const response = await fetch(`http://8.155.5.178:8080/api/entity/${this.selectedItem.name}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json'
          }
        });

        const data = await response.json();
        
        if (response.ok && data.code === 1) {
          console.log('Item approved successfully:', this.selectedItem);
          alert('成功删除');
          this.fetchUncheckedPaginatedResults(this.uncheckedCurrentPage, this.pageSize);
        } else {
          console.error('Failed to delete item:', data);
          alert('删除失败');
        }
      } catch (error) {
        console.error('Error deleting item:', error);
        alert('删除失败');
      } finally {
        this.closeModal();
      }
    },
  },
  mounted() {
    this.fetchUncheckedPaginatedResults(this.uncheckedCurrentPage, this.pageSize); 
  }
};
</script>

<style scoped>
.expert-review {
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

.pagination {
  display: flex;
  align-items: center;
}

.pagination button {
  padding: 5px 10px;
  border: none;
  background-image: linear-gradient(#45496a,#7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 4px;
  margin: 0 5px;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.review-list {
  flex: 1;
  width: 100%;
}

.review-list table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.review-list th, .review-list td {
  padding: 10px;
  text-align: center;
  border: 1px solid #ccc;
  overflow: hidden;
  text-overflow: ellipsis;
}

.review-list th {
  background-color: #f0f0f0;
}

.review-list td button {
  padding: 5px 10px;
  background-image: linear-gradient(#45496a,#7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 4px;
  border: none;
}

.review-list td button:hover {
  background-image: linear-gradient(#a1a3b4,#b1a9b9);
}

.review-list th.col-name,
.review-list td.col-name {
  width: 10%;
}

.review-list th.col-overview,
.review-list td.col-overview {
  width: 70%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.review-list th.col-uploader,
.review-list td.col-uploader {
  width: 10%;
}

.review-list th.col-action,
.review-list td.col-action {
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
  font-family: Arial, sans-serif;
}

.modal-content h2 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.modal-content div {
  margin-bottom: 15px;
}

.modal-content label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

.modal-content input,
.modal-content textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
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

.description-text {
  text-indent: 2em;
}

.modal-content strong {
  display: block; 
  margin-bottom: 5px; 
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.modal-actions button {
  padding: 10px 20px;
  border: none;
  background-image: linear-gradient(#45496a,#7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 4px;
  margin-left: 10px;
}

.modal-actions button:hover {
  background-image: linear-gradient(#a1a3b4,#b1a9b9);
}
</style>