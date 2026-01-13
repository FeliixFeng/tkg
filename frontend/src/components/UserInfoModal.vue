<template>
  <div v-if="isModalVisible" class="modal-overlay">
    <div class="modal" @click.stop>
      <div class="modal-header">
        <h2>{{ currentAction === 'changePassword' ? '修改密码' : currentAction === 'edit' ? '编辑用户信息' : '用户信息' }}</h2>
        <div class="close-button" @click="closeModal">&times;</div>
      </div>
      <div v-if="user" class="modal-content">
        <form v-if="isEditing" @submit.prevent="saveUser" class="form-aligned">
          <!-- 只在不是修改密码时显示用户名和手机号 -->
          <div class="form-group" v-if="!isChangingPassword">
            <label for="username">用户名:</label>
            <input id="username" v-model="user.username" type="text" required />
          </div>
          <div class="form-group" v-if="!isChangingPassword">
            <label for="phone">手机号:</label>
            <input id="phone" v-model="user.phone" type="tel" required />
          </div>
          <!-- 修改密码时显示旧密码、新密码和确认密码 -->
          <div class="form-group" v-if="isChangingPassword">
            <label for="oldPassword">旧密码:</label>
            <input id="oldPassword" v-model="user.oldPassword" type="password" required />
          </div>
          <div class="form-group" v-if="isChangingPassword">
            <label for="newPassword">新密码:</label>
            <input id="newPassword" v-model="user.newPassword" type="password" required />
          </div>
          <div class="form-group" v-if="isChangingPassword">
            <label for="confirmPassword">确认密码:</label>
            <input id="confirmPassword" v-model="user.confirmPassword" type="password" required />
          </div>
          <div class="form-buttons">
            <button type="submit" class="btn btn-primary">保存</button>
            <button @click="cancelEdit" class="btn btn-secondary">取消</button>
          </div>
        </form>
        <div v-else class="user-info">
          <p><strong>用户名:</strong> {{ user.username }}</p>
          <p><strong>手机号:</strong> {{ user.phone }}</p>
          <div class="action-buttons">
            <button @click="editUser" class="btn btn-primary">编辑信息</button>
            <button @click="changePassword" class="btn btn-primary">修改密码</button>
          </div>
        </div>
      </div>
      <div v-else class="loading">
        <p>加载中...</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from '../http';

export default {
  data() {
    return {
      isModalVisible: false,
      user: null,
      isEditing: false,
      isChangingPassword: false,
      originalUser: null ,// 用于存储原始用户数据
      currentAction: '' // 新增变量用于跟踪当前操作
    };
  },
  methods: {
    editUser() {
      this.originalUser = { ...this.user }; // 保存原始用户数据
      this.isEditing = true;
      this.isChangingPassword = false;
      this.currentAction = 'edit'; 
    },
    changePassword() {
      this.originalUser = { ...this.user }; 
      this.isEditing = true;
      this.isChangingPassword = true;
      this.currentAction = 'changePassword'; 
    },
    cancelEdit() {
      this.user = { ...this.originalUser }; 
      this.isEditing = false;
      this.isChangingPassword = false;
      this.currentAction = 'view'; // 恢复为查看模式
    },
    showModal() {
      this.isModalVisible = true;
      this.fetchUser();
      this.currentAction = 'view'; 
    },
    closeModal() {
      this.isModalVisible = false;
      this.isEditing = false;
      this.isChangingPassword = false;
      this.currentAction = 'view'; 
    },
    async fetchUser() {
      try {
        const username = sessionStorage.getItem('username'); 
        console.log('Username:', username); // 输出用户名
        const response = await axios.get(`/api/user/get_user/${username}`);
        console.log('Fetched User Data:', response.data); // 输出获取到的用户数据
        this.user = response.data.data; 
      } catch (error) {
        console.error('Error fetching user:', error);
        alert('加载用户信息失败');
      }
    },
    async saveUser() {
      if (this.isChangingPassword) {
        if (this.user.oldPassword !== sessionStorage.getItem('password')) {
          alert('旧密码不正确');
          return;
        }
        if (this.user.newPassword !== this.user.confirmPassword) {
          alert('新密码不一致，请重新输入');
          return;
        }
        try {
          await axios.put('/api/user/modify_password', {
            id: this.user.id,
            password: this.user.newPassword
          });
          alert('密码修改成功');
        } catch (error) {
          console.error('Error changing password:', error);
          alert('密码修改失败');
        }
      } else {
        if (this.user.username !== this.originalUser.username) {
          try {
            const response = await axios.get(`/api/user/get_user/${this.user.username}`);
            if (response.data && response.data.data) {
              alert('用户名已存在');
              return;
            }
          } catch (error) {
            // 如果接口返回404或其他错误，说明用户名不存在
            if (error.response && error.response.status === 404) {
              // 用户名不存在，继续保存操作
            } else {
              console.error('Error checking username:', error);
              alert('用户名检查失败');
              return;
            }
          }
        }
        try {
          await axios.put('/api/user/update', {
            id: this.user.id,
            username: this.user.username,
            phone: this.user.phone
          });
          alert('用户信息修改成功');
        } catch (error) {
          console.error('Error updating user:', error);
          alert('用户信息修改失败');
        }
      }
      this.closeModal();
    }
  },
  created() {
    // 获取用户信息
    this.fetchUser();
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 600px;
  width: 100%;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px; /* 调整顶部距离 */
}

.close-button {
  cursor: pointer;
  font-size: 24px;
  color: red;
}

.modal-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px; /* 调整顶部距离 */
}

.form-aligned {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-group label {
  display: inline-block;
  width: 100px;
  text-align: right;
}

.form-group input {
  display: inline-block;
  width: 200px; 
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.loading {
  text-align: center;
  padding: 20px;
}
</style>