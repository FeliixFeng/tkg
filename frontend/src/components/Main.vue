<template>
  <div id="Main">
    <header>
      <div class="header-top">
        <h1>纺织知识图谱</h1>
      </div>
      <div class="header-bottom">
        <div class="user-avatar" @click="showUserInfo">
          <img :src="defaultUserAvatar" alt="用户头像" />
          <span class="username">{{ userName }}</span>
        </div>
      </div>
    </header>

    <div class="main-content">
      <nav class="sidebar">
        <ul>
          <li
            @click="navigateTo('knowledgeGraph')"
            :class="{ active: activeSection === 'knowledgeGraph' }"
          >
            知识图谱
          </li>
          <li
            @click="navigateTo('knowledgeSearch')"
            :class="{ active: activeSection === 'knowledgeSearch' }"
          >
            知识检索
          </li>
          <li
            v-if="userType === 1"
            @click="navigateTo('expertReview')"
            :class="{ active: activeSection === 'expertReview' }"
          >
            专家评审
          </li>
          <li
            v-if="userType === 0"
            @click="navigateTo('knowledgeImport')"
            :class="{ active: activeSection === 'knowledgeImport' }"
          >
            知识导入
          </li>
          <li
            v-if="userType === 0"
            @click="navigateTo('myKnowledge')"
            :class="{ active: activeSection === 'myKnowledge' }"
          >
            我的知识
          </li>
        </ul>
      </nav>

      <div class="content">
        <button v-if="!isInitialView" @click="returnToInitialView" class="back-button">
          返回
        </button>

        <h2 v-if="isInitialView">{{ currentSectionTitle }}</h2>
        <p v-if="isInitialView">{{ currentSectionContent }}</p>

        <div v-if="isInitialView" class="welcome-images">
          <img src="@/assets/tex1.jpg" alt="纺织图片" class="left-image" />
          <img src="@/assets/tex2.jpg" alt="纺织图片" class="middle-image" />
          <img src="@/assets/tex3.jpg" alt="纺织图片" class="right-image" />
        </div>

        <div v-if="currentSection === 'expertReview'" class="review-area">
          <expert-review />
        </div>

        <div v-if="currentSection === 'knowledgeGraph'" class="graph-area">
          <knowledge-graph :data="knowledgeGraphData" @showInfo="showInfo" />
        </div>

        <knowledge-search v-if="currentSection === 'knowledgeSearch'" :on-show-info="showInfo" />

        <knowledge-import v-if="currentSection === 'knowledgeImport'" />

        <div v-if="currentSection === 'myKnowledge'" class="knowledge-area">
          <my-knowledge />
        </div>
      </div>

      <div v-if="infoPanelVisible" class="info-panel">
        <div class="info-header">
          <h3>{{ infoTitle }}</h3>
          <button class="close-button" @click="closeInfoPanel">✖</button>
        </div>
        <div class="info-content">
          <img :src="infoImageUrl" alt="信息图片" class="info-image" />
          <div class="info-description">
            <h4>特点</h4>
            <p>{{ entityDetails ? entityDetails.feature : '暂无特点' }}</p>
            <h4>详情描述</h4>
            <p v-html="infoContent"></p>
          </div>
        </div>
      </div>
    </div>

    <user-info-modal ref="userInfoModal" />
  </div>
</template>

<script>
import UserInfoModal from './UserInfoModal.vue';
import KnowledgeGraph from './KnowledgeGraph.vue';
import ExpertReview from './ExpertReview.vue';
import KnowledgeSearch from './KnowledgeSearch.vue';
import KnowledgeImport from './KnowledgeImport.vue';
import MyKnowledge from './MyKnowledge.vue';

export default {
  components: {
    UserInfoModal,
    KnowledgeGraph,
    ExpertReview,
    KnowledgeSearch,
    KnowledgeImport,
    MyKnowledge,
  },
  data() {
    return {
      defaultUserAvatar:  require('@/assets/defaultavatar.jpg'), // 默认用户头像
      currentSectionTitle: '欢迎使用面向纺织行业的知识库管理系统！',
      currentSectionContent: '这是一个专为纺织行业设计的知识管理平台，为您提供丰富的知识资源和便捷的操作体验。',
      currentSection: '', 
      infoPanelVisible: false,  // 控制信息栏显示状态
      infoTitle: '',
      infoContent: '',
      defaultInfoImageUrl: require('@/assets/defaultInfoImage.jpg'),
      isInitialView: true, 
      activeSection: '', 
      knowledgeGraphData: {}, 
      entityDetails: null, // 存储从后端获取的详细信息
    };
  },
  mounted() {
    this.fetchKnowledgeGraphData();
  },
  methods: {
    showUserInfo() {
      this.$refs.userInfoModal.showModal();
    },
    navigateTo(section) {
      this.isInitialView = false;
      this.infoPanelVisible = false;

      switch (section) {
        case 'knowledgeGraph':
          this.currentSectionTitle = '知识图谱';
          this.currentSection = 'knowledgeGraph';
          this.activeSection = 'knowledgeGraph';
          break;
        case 'knowledgeImport':
          this.currentSectionTitle = '知识导入';
          this.currentSection = 'knowledgeImport';
          this.activeSection = 'knowledgeImport';
          break;
        case 'expertReview':
          this.currentSectionTitle = '专家评审';
          this.currentSection = 'expertReview';
          this.activeSection = 'expertReview';
          break;
        case 'knowledgeSearch':
          this.currentSectionTitle = '知识检索';
          this.currentSection = 'knowledgeSearch';
          this.activeSection = 'knowledgeSearch';
          this.searchResults = []; 
          this.currentPage = 1; 
          break;
        case 'myKnowledge': 
          this.currentSectionTitle = '我的知识';
          this.currentSection = 'myKnowledge';
          this.activeSection = 'myKnowledge';
          break;
      }
    },
    returnToInitialView() {
      this.isInitialView = true;
      this.currentSectionTitle = '欢迎使用面向纺织行业的知识库管理系统！';
      this.currentSectionContent = '这是一个专为纺织行业设计的知识管理平台，为您提供丰富的知识资源和便捷的操作体验。';
      this.currentSection = '';
      this.activeSection = '';
    },
    searchGraph(node, query) {
      if (node.name && node.name.toLowerCase().includes(query)) {
        this.getShortDescription(node.name).then(shortDescription => {
          this.searchResults.push({
            name: node.name,
            shortDescription: shortDescription,
            id: node.id
          });
        });
      }
      if (node.children) {
        node.children.forEach(child => this.searchGraph(child, query));
      }
    },
    async showInfo(node) {
      if (!node || !node.id) {
      console.error('Invalid node or missing id:', node);
      return;
    }
      this.infoPanelVisible = true;
      this.infoTitle = node.name;

      try {
        const response = await fetch(`http://8.155.5.178:8080/api/entity/${node.id}`);
        const data = await response.json();

        if (response.ok && data.code === 1) {
          this.entityDetails = data.data;
          this.infoImageUrl = this.entityDetails.imageUrl || this.defaultInfoImageUrl;
          this.infoContent = this.entityDetails.description;
        } else {
          this.entityDetails = null;
          this.infoImageUrl = this.defaultInfoImageUrl;
          this.infoContent = '暂无描述';
        }
      } catch (error) {
        console.error('请求实体详细信息时发生错误:', error);
        this.entityDetails = null;
        this.infoImageUrl = this.defaultInfoImageUrl;
        this.infoContent = '暂无描述';
      }
    },
    getDescriptionById(id) {
      function findNode(data, nodeId) {
        if (data.id === nodeId) {
          return data;
        }
        if (data.children) {
          for (let child of data.children) {
            const foundNode = findNode(child, nodeId);
            if (foundNode) {
              return foundNode;
            }
          }
        }
        return null;
      }

      const node = findNode(this.knowledgeGraphData, id);
      if (node) {
        return node.description || ''; // 如果节点有描述信息，则返回，否则返回空字符串
      }
      return '未找到对应节点';
    },
    closeInfoPanel() {
      this.infoPanelVisible = false;
    },
    async fetchKnowledgeGraphData() {
      try {
        const response = await fetch('http://8.155.5.178:8080/api/entity/tree');
        const data = await response.json();
      
        if (response.ok) { // 检查 HTTP 请求是否成功
          if (data.code === 1) {
            this.knowledgeGraphData = data.data;
          } else if (data.code === 0) {
            const errorMessage = data.msg || '未知错误';
            alert(`获取知识图谱数据失败: ${errorMessage}`);
          } else {
            alert('获取知识图谱数据失败，请稍后再试');
          }
        } else {
          console.error('HTTP 请求失败:', response.status, response.statusText);
          alert('网络请求失败，请检查网络连接');
        }
      } catch (error) {
        console.error('请求知识图谱数据时发生错误:', error);
        alert('网络请求失败，请检查网络连接');
      }
    },
  },
  computed: {
    userName() {
      return sessionStorage.getItem('username');
    },
    userType() {
      return parseInt(sessionStorage.getItem('userType'), 10) || 0;
    }
  }
};
</script>

<style scoped>
#Main {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

header {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #1d2347;
  color: white;
  height: 60px;
}

.header-top h1 {
  padding: 8px;
}

.header-bottom {
  flex: 2;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.user-avatar {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-avatar .username {
  font-size: 18px;
  color: rgb(222, 203, 203);
}

.main-content {
  display: flex;
  flex: 1;
  height: calc(100vh - 60px); /* 减去头部高度，确保内容适应屏幕 */
}

.sidebar {
  width: 200px;
  background-color: #f4f4f4;
  padding: 10px;
  height: 100%; /* 确保侧边栏与屏幕高度一致 */
  flex-shrink: 0;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
}

.sidebar li {
  padding: 10px;
  cursor: pointer;
}

.sidebar li:hover {
  background-color: #e0e0e0;
}

.sidebar li.active {
  background-color: #a4abd1;
  color: white;
}

.content {
  flex: 1;
  padding: 20px;
  box-sizing: border-box;
  position: relative; /* 为返回按钮提供定位参照 */
  overflow: auto; /*仅内容部分可滚动 */
}

.back-button {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 5px 10px;
  border: none;
  background-image: linear-gradient(#45496a,#7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 4px;
}

.back-button:hover {
  background-image: linear-gradient(#a1a3b4,#b1a9b9); 
}

.welcome-images {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.welcome-images img {
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.welcome-images img:hover {
  transform: scale(1.1);
}

.left-image {
  width: 350px;
  height: 300px;
  margin-right: -30px;
  z-index: 1;
}

.middle-image {
  width: 380px;
  height: 350px;
  z-index: 2;
}

.right-image {
  width: 350px;
  height: 300px;
  margin-left: -30px;
  z-index: 1;
}

.info-panel {
  width: 350px;
  background: white;
  border: 1px solid #ccc;
  padding: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-left: 20px;
  box-sizing: border-box;
  height: 100%;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.info-header h3 {
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #4159df;
}

.close-button:hover {
  color: #333a65;
}

.info-content {
  text-align: left;
  margin-left: 20px;
}

.info-image {
  width: 100%;
  max-width: 200px;
  height: auto;
  border-radius: 8px;
  margin-bottom: 10px;
  margin-left: 20px;
}

.info-description {
  text-align: left;
  margin-left: 20px;
}

.info-description h4 {
  margin-top: 10px;
  margin-bottom: 5px;
}
</style>
