<template>
  <div class="page">
    <el-card shadow="never">
      <div class="toolbar">
        <el-upload
          :show-file-list="false"
          :before-upload="beforeUpload"
          :http-request="handleUpload"
          accept=".txt,.doc,.docx,.pdf,.md,.markdown"
        >
          <el-button type="primary">上传文档</el-button>
        </el-upload>
        <span class="tip">支持 txt / doc / docx / pdf / md 格式</span>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="fileName" label="文件名" min-width="160" />
        <el-table-column prop="fileType" label="类型" width="90" />
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="vectorStatus" label="向量化状态" width="120">
          <template #default="{ row }">
            <el-tag :type="vectorTagType(row.vectorStatus)">{{ vectorLabel(row.vectorStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="chunkCount" label="分块数" width="90" />
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="titleDialogVisible" title="设置文档标题" width="400px">
      <el-input v-model="uploadTitle" placeholder="请输入文档标题" />
      <template #footer>
        <el-button @click="titleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="confirmUpload">确定上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listKnowledge, uploadKnowledge, deleteKnowledge } from '../api/knowledge'

const loading = ref(false)
const uploading = ref(false)
const tableData = ref([])
const titleDialogVisible = ref(false)
const uploadTitle = ref('')
const pendingFile = ref(null)

function formatSize(bytes) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}

function vectorLabel(status) {
  const map = { PENDING: '处理中', DONE: '已完成', FAILED: '失败' }
  return map[status] || status
}

function vectorTagType(status) {
  const map = { PENDING: 'warning', DONE: 'success', FAILED: 'danger' }
  return map[status] || 'info'
}

async function fetchList() {
  loading.value = true
  try {
    tableData.value = await listKnowledge()
  } finally {
    loading.value = false
  }
}

function beforeUpload(file) {
  pendingFile.value = file
  uploadTitle.value = file.name.replace(/\.[^.]+$/, '')
  titleDialogVisible.value = true
  return false
}

async function handleUpload() {
  // handled by beforeUpload + confirmUpload
}

async function confirmUpload() {
  if (!uploadTitle.value.trim()) {
    ElMessage.warning('请输入文档标题')
    return
  }
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', pendingFile.value)
    formData.append('title', uploadTitle.value.trim())
    await uploadKnowledge(formData)
    ElMessage.success('上传成功')
    titleDialogVisible.value = false
    pendingFile.value = null
    fetchList()
  } finally {
    uploading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除文档「${row.title}」吗？`, '提示', { type: 'warning' })
  await deleteKnowledge(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page {
  padding: 4px;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.tip {
  color: #909399;
  font-size: 13px;
}
</style>
