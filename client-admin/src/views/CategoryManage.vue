<template>
  <div class="page">
    <el-card shadow="never">
      <div class="toolbar">
        <el-button type="primary" @click="openDialog()">新增分类</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="160" />
        <el-table-column prop="parentId" label="父分类ID" width="100">
          <template #default="{ row }">{{ row.parentId || '顶级' }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑分类' : '新增分类'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="如：手机通讯" />
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="form.parentId" placeholder="顶级分类选 0" style="width: 100%">
            <el-option label="顶级分类" :value="0" />
            <el-option
              v-for="cat in parentCategories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
              :disabled="editing && cat.id === editId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategories, createCategory, updateCategory, deleteCategory } from '../api/product'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref(null)
const formRef = ref()

const form = reactive({
  name: '',
  parentId: 0,
  sort: 0,
  status: 1,
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
}

const parentCategories = computed(() =>
  tableData.value.filter((c) => !c.parentId || c.parentId === 0),
)

async function fetchList() {
  loading.value = true
  try {
    tableData.value = await getCategories()
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  editing.value = !!row
  editId.value = row?.id || null
  Object.assign(form, {
    name: row?.name || '',
    parentId: row?.parentId ?? 0,
    sort: row?.sort ?? 0,
    status: row?.status ?? 1,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editing.value) {
      await updateCategory(editId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createCategory({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteCategory(row.id)
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
}
</style>
