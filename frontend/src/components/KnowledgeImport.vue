<template>
  <div class="import-form">
    <h3>知识导入</h3>
    <form @submit.prevent="submitKnowledge">
      <div>
        <label for="name">名称:</label>
        <input type="text" id="name" v-model="entity.name" required />
      </div>
      <div>
        <label for="parentName">父节点名称:</label>
        <select id="parentName" v-model="entity.parentId">
          <option value="null">根节点</option>
          <option v-for="option in flatOptions" :key="option.id" :value="option.id">{{ option.name }}</option>
        </select>
      </div>
      <div>
        <label for="overview">简述:</label>
        <textarea id="overview" v-model="entity.overview" required></textarea>
      </div>
      <div>
        <label for="feature">特点:</label>
        <textarea id="feature" v-model="entity.feature"></textarea>
      </div>
      <div>
        <label for="description">详情描述:</label>
        <textarea id="description" v-model="entity.description" required></textarea>
      </div>
      <div>
        <label for="image">上传图片:</label>
        <input type="file" id="image" @change="handleFileUpload" ref="fileInput" />
      </div>
      <div class="button-container">
        <button type="submit">提交</button>
        <button type="button" @click="clearForm">取消</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  computed: {
    userid() {
      const userId = sessionStorage.getItem('userId');
      console.log('userId from sessionStorage:', userId);
      return userId !== null && userId !== 'undefined' ? parseInt(userId, 10) : 0;
    }
  },
  data() {
    return {
      entity: {
        name: '',
        parentName: '',
        overview: '',
        feature: '',
        description: '',
        imageUrl: null,
        parentId: null,
      },
      file: null,
      treeData: [],
      flatOptions: [],
    };
  },
  created() {
    this.fetchTreeData();
  },
  methods: {
    async fetchTreeData() {
      try {
        const response = await axios.get('http://8.155.5.178:8080/api/entity/tree');
        this.treeData = response.data.data;
        this.flatOptions = this.flattenTree(this.treeData);
      } catch (error) {
        console.error('Error fetching tree data:', error);
      }
    },
    flattenTree(data) {
      return data.reduce((acc, item) => {
        acc.push({ id: item.id, name: item.name });
        if (item.children && item.children.length > 0) {
          acc.push(...this.flattenTree(item.children));
        }
        return acc;
      }, []);
    },
    clearForm() {
      this.entity.name = '';
      this.entity.parentName = '';
      this.entity.overview = '';
      this.entity.feature = '';
      this.entity.description = '';
      this.entity.imageUrl = null;
      this.entity.parentId = null;
      this.file = null;

      // 清空文件输入字段
      this.$refs.fileInput.value = '';
    },
    handleFileUpload(event) {
      this.file = event.target.files[0];
    },
    async getEntityInfoByName(name) {
      try {
        const response = await axios.get(`http://8.155.5.178:8080/api/entity/all/${encodeURIComponent(name)}`, {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
        });
        const entityInfo = response.data.data;
        // 检查实体是否存在且 status 为 1
        if (entityInfo && entityInfo.status === 1) {
          return entityInfo;
        }
        return null;
      } catch (error) {
        console.error('Error getting entity info by name:', error);
        return null;
      }
    },
    async submitKnowledge() {
      try {
        // 检查实体名称是否重复
        const entityInfo = await this.getEntityInfoByName(this.entity.name);
        if (entityInfo) {
          alert('实体名称已存在，请重新输入');
          return;
        }

        let imageUrl = '';

        if (this.file) {
          const formData = new FormData();
          formData.append('file', this.file);

          const uploadResponse = await axios.post('http://8.155.5.178:8080/api/upload', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });

          imageUrl = uploadResponse.data.data;
        }

        const userId = this.userid; // 获取 userId
        console.log('UserId from sessionStorage:', userId);

        if (!userId) {
          alert('用户ID未找到，请重新登录');
          return;
        }

        const entityData = {
          ...this.entity,
          imageUrl: imageUrl,
          status: 0, 
          userId: userId,
        };

        const response = await axios.post('http://8.155.5.178:8080/api/entity/add', entityData);

        if (response.data.code === 1) {
          alert('知识提交成功，等待专家评审');

          // 通过名称查询新实体的ID
          const entityId = await this.getIdByName(this.entity.name);

          if (!entityId) {
            console.error('无法获取新实体的ID');
            return;
          }

          // 更新 treeData
          const newEntity = {
            id: entityId,
            name: this.entity.name,
            children: [],
          };

          if (this.entity.parentId === null) {
            // 如果是根节点，直接添加到 treeData
            this.treeData.push(newEntity);
          } else {
            // 否则，找到父节点并添加到其 children 中
            const parentNode = this.findNodeById(this.treeData, this.entity.parentId);
            if (parentNode) {
              parentNode.children.push(newEntity);
            }
          }

          // 通知 KnowledgeGraph 组件更新数据
          this.$emit('updateTreeData', this.treeData);

          this.clearForm();
        } else {
          alert('知识提交失败，请重试');
        }
      } catch (error) {
        console.error('Error:', error);
        alert('网络错误，请重试');
      }
    },
      async getIdByName(name) {
        try {
          const response = await axios.get(`http://8.155.5.178:8080/api/entity/get_id/${encodeURIComponent(name)}`);
          return response.data.data; 
        } catch (error) {
          console.error('Error getting entity ID by name:', error);
          return null;
        }
    },
    findNodeById(treeData, id) {
      for (let node of treeData) {
        if (node.id === id) {
          return node;
        }
        if (node.children && node.children.length > 0) {
          const foundNode = this.findNodeById(node.children, id);
          if (foundNode) {
            return foundNode;
          }
        }
      }
      return null;
    }
  },
};
</script>


<style scoped>
.import-form {
  margin-top: 20px;
}

.import-form h3 {
  margin-bottom: 10px;
}

.import-form div {
  margin-bottom: 15px;
}

.import-form label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.import-form input[type="text"],
.import-form textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.import-form input[type="file"] {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  height: 15vh;
}

.import-form button {
  padding: 10px 20px;
  border: none;
  background-image: linear-gradient(#45496a,#7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 4px;
  margin-right: 10px;
}

.import-form button:hover {
  background-image: linear-gradient(#a1a3b4,#b1a9b9);
}

.import-form .button-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.import-form .button-container button:last-child {
  background-color: #ccc;
  color: #333;
}

.import-form .button-container button:last-child:hover {
  background-color: #aaa;
}

.import-form select {
  width: 100%; 
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>