<template>
  <div id="dashboard">
    <header>
      <div class="header-top">
        <h1 class="brand-title-clickable" @click="goHome">纺织知识图谱</h1>
      </div>
      <div class="header-bottom">
        <div class="user-info">
          <div class="user-avatar" @click="showUserInfo">
            <img :src="userAvatar" alt="用户头像" />
             <span class="username" v-if="currentUser">{{ currentUser?.username }}</span>
          </div>
           <el-button type="danger" size="small" @click="handleLogout">
             退出登录
           </el-button>
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
             v-if="currentUser && currentUser.userType == 1"
             @click="navigateTo('expertReview')"
             :class="{ active: activeSection === 'expertReview' }"
           >
             专家评审
           </li>
            <li
              v-if="currentUser && currentUser.userType == 0"
              @click="navigateTo('knowledgeImport')"
              :class="{ active: activeSection === 'knowledgeImport' }"
            >
              知识导入
            </li>
           <li
              v-if="currentUser && currentUser.userType == 0"
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
import { mapState, mapActions } from 'vuex';
import { ElMessageBox } from 'element-plus';
import UserInfoModal from '@/components/UserInfoModal.vue';
import KnowledgeGraph from '@/components/KnowledgeGraph.vue';
import ExpertReview from '@/components/ExpertReview.vue';
import KnowledgeSearch from '@/components/KnowledgeSearch.vue';
import KnowledgeImport from '@/components/KnowledgeImport.vue';
import MyKnowledge from '@/components/MyKnowledge.vue';
import http from '@/http';

export default {
  name: 'Dashboard',
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
      currentSectionTitle: '欢迎使用面向纺织行业的知识库管理系统！',
      currentSectionContent: '这是一个专为纺织行业设计的知识管理平台，为您提供丰富的知识资源和便捷的操作体验。',
      currentSection: '',
      infoPanelVisible: false,
      infoTitle: '',
      infoContent: '',
      defaultInfoImageUrl: new URL('@/assets/defaultInfoImage.jpg', import.meta.url).href,
       defaultUserAvatar: require('@/assets/defaultavatar.jpg'),
      isInitialView: true,
      activeSection: '',
       knowledgeGraphData: [],
      entityDetails: null,
    };
  },
  computed: {
    ...mapState('auth', ['user']),
    currentUser() {
      return this.user;
    },
    userAvatar() {
      if (!this.currentUser || !this.currentUser.avatar || this.currentUser.avatar === 'default.png') {
        return this.defaultUserAvatar;
      }
      return this.currentUser.avatar;
    }
  },
  mounted() {
    // 恢复之前保存的 section 状态（页面刷新后）
    const savedSection = sessionStorage.getItem('activeSection');
    if (savedSection) {
      this.navigateTo(savedSection);
    }
    // 如果没有保存的状态，默认显示欢迎主页（不调用任何navigateTo）
  },
  methods: {
    ...mapActions('auth', ['logout']),

    showUserInfo() {
      this.$refs.userInfoModal.showModal();
    },

    async handleLogout() {
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        });
        await this.logout();
        this.$router.push('/login');
      } catch (error) {
        console.log('取消退出');
      }
    },

    navigateTo(section) {
      this.isInitialView = false;
      this.infoPanelVisible = false;

      // 保存当前 section 到 sessionStorage，用于页面刷新后恢复
      sessionStorage.setItem('activeSection', section);

      switch (section) {
        case 'knowledgeGraph':
          this.currentSectionTitle = '知识图谱';
          this.currentSection = 'knowledgeGraph';
          this.activeSection = 'knowledgeGraph';
          this.fetchKnowledgeGraphData();
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
      this.infoPanelVisible = false;
      // 清除保存的 section 状态
      sessionStorage.removeItem('activeSection');
    },

    goHome() {
      this.returnToInitialView();
    },

    async showInfo(node) {
      if (!node || !node.id) {
        console.error('Invalid node or missing id:', node);
        return;
      }
      this.infoPanelVisible = true;
      this.infoTitle = node.name;

      try {
        const response = await http.get(`/api/entity/${node.id}`);
        const data = response.data;

        if (response.status === 200 && data.code === 1) {
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

    closeInfoPanel() {
      this.infoPanelVisible = false;
    },

    async fetchKnowledgeGraphData() {
      try {
        const response = await http.get('/api/entity/tree');
        const data = response.data;

        if (response.status === 200 && data.code === 1) {
          this.knowledgeGraphData = data.data;
        } else {
          console.error('获取知识图谱数据失败:', data.msg);
        }
      } catch (error) {
        console.error('请求知识图谱数据时发生错误:', error);
      }
    },
  },
};
</script>

<style scoped>
#dashboard {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #1d2347;
  color: white;
  height: 60px;
}

.header-top h1 {
  padding: 8px;
  margin: 0;
}

.brand-title-clickable {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 4px;
}

.brand-title-clickable:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: scale(1.02);
}

.header-bottom {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-avatar {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.user-avatar:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-avatar .username {
  font-size: 16px;
  color: #decacb;
}

.main-content {
  display: flex;
  flex: 1;
  height: calc(100vh - 60px);
}

.sidebar {
  width: 200px;
  background-color: #f4f4f4;
  padding: 10px;
  height: 100%;
  flex-shrink: 0;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.sidebar li {
  padding: 12px 15px;
  cursor: pointer;
  border-radius: 5px;
  margin-bottom: 5px;
  transition: background-color 0.3s;
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
  position: relative;
  overflow: auto;
}

.back-button {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 6px 12px;
  border: none;
  background-image: linear-gradient(#45496a, #7d8bae);
  color: white;
  cursor: pointer;
  border-radius: 4px;
  transition: background-image 0.3s;
}

.back-button:hover {
  background-image: linear-gradient(#a1a3b4, #b1a9b9);
}

.welcome-images {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  gap: 20px;
}

.welcome-images img {
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  width: 33%;
  max-width: 380px;
}

.welcome-images img:hover {
  transform: scale(1.05);
}

.info-panel {
  width: 350px;
  background: white;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-left: 20px;
  box-sizing: border-box;
  height: 100%;
  overflow-y: auto;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.info-header h3 {
  margin: 0;
  font-size: 18px;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #4159df;
  padding: 0 5px;
  line-height: 1;
}

.close-button:hover {
  color: #333a65;
}

.info-content {
  text-align: left;
}

.info-image {
  width: 100%;
  max-width: 280px;
  height: auto;
  border-radius: 8px;
  margin-bottom: 15px;
}

.info-description {
  text-align: left;
}

.info-description h4 {
  margin-top: 15px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.info-description p {
  margin: 0;
  line-height: 1.6;
  color: #333;
}
</style>
