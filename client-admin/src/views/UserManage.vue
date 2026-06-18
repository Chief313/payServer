<template>
  <div class="page">
    <el-card shadow="never">
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索用户名 / 昵称 / 手机 / 邮箱"
          clearable
          style="width: 260px"
          @keyup.enter="fetchList"
          @clear="fetchList"
        />
        <el-select v-model="roleFilter" placeholder="角色" clearable style="width: 130px" @change="fetchList">
          <el-option label="普通用户" value="USER" />
          <el-option label="管理员" value="ADMIN" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 130px" @change="fetchList">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="openDialog()">新增用户</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column prop="phone" label="手机" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="110">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑用户' : '新增用户'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="editing" />
        </el-form-item>
        <el-form-item :label="editing ? '新密码' : '密码'" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            :placeholder="editing ? '留空则不修改密码' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="USER">普通用户</el-radio>
            <el-radio value="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, createUser, updateUser, deleteUser } from '../api/user-manage'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const roleFilter = ref('')
const statusFilter = ref(null)
const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref(null)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  role: 'USER',
  status: 1,
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

function roleLabel(role) {
  return role === 'ADMIN' ? '管理员' : '普通用户'
}

async function fetchList() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (roleFilter.value) params.role = roleFilter.value
    if (statusFilter.value !== null && statusFilter.value !== '') params.status = statusFilter.value
    const res = await getUsers(params)
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  editing.value = !!row
  editId.value = row?.id || null
  Object.assign(form, {
    username: row?.username || '',
    password: '',
    nickname: row?.nickname || '',
    phone: row?.phone || '',
    email: row?.email || '',
    role: row?.role || 'USER',
    status: row?.status ?? 1,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  if (!editing.value && !form.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }
  submitting.value = true
  try {
    const data = {
      username: form.username,
      nickname: form.nickname,
      phone: form.phone,
      email: form.email,
      role: form.role,
      status: form.status,
    }
    if (form.password.trim()) data.password = form.password.trim()
    if (editing.value) {
      await updateUser(editId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createUser(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', { type: 'warning' })
  await deleteUser(row.id)
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
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
