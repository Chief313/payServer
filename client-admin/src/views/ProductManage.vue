<template>
  <div class="page">
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="filterCategoryId" placeholder="全部分类" clearable style="width: 200px" @change="fetchList">
          <el-option label="全部分类" :value="null" />
          <el-option-group v-for="parent in parentCategories" :key="parent.id" :label="parent.name">
            <el-option :label="parent.name + '（全部）'" :value="parent.id" />
            <el-option
              v-for="child in getChildren(parent.id)"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-option-group>
        </el-select>
        <el-button type="primary" @click="openDialog()">新增商品</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="160" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑商品' : '新增商品'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="封面" prop="coverUrl">
          <div class="cover-row">
            <el-input v-model="form.coverUrl" placeholder="上传后自动填充，或手动输入 URL" />
            <el-upload
              :show-file-list="false"
              accept="image/*"
              :before-upload="beforeCoverUpload"
            >
              <el-button type="primary" plain>上传图片</el-button>
            </el-upload>
          </div>
          <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" alt="封面预览" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option-group v-for="parent in parentCategories" :key="parent.id" :label="parent.name">
              <el-option
                v-for="child in getChildren(parent.id)"
                :key="child.id"
                :label="child.name"
                :value="child.id"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="!editing">
          <el-form-item label="默认规格" prop="skuName">
            <el-input v-model="form.skuName" placeholder="例如：标准版" />
          </el-form-item>
          <el-form-item label="库存" prop="stock">
            <el-input-number v-model="form.stock" :min="0" :precision="0" style="width: 100%" />
          </el-form-item>
        </template>
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
import { getProducts, createProduct, updateProduct, deleteProduct, getCategories, createSku } from '../api/product'
import { uploadPic } from '../api/pic'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const categories = ref([])
const filterCategoryId = ref(null)
const total = ref(0)
const page = ref(1)
const size = ref(10)
const dialogVisible = ref(false)
const editing = ref(false)
const editId = ref(null)
const formRef = ref()

const form = reactive({
  name: '',
  description: '',
  price: 0,
  coverUrl: '',
  categoryId: 1,
  status: 1,
  skuName: '默认规格',
  stock: 100,
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  skuName: [{ required: true, message: '请输入默认规格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
}

const parentCategories = computed(() =>
  categories.value.filter((c) => !c.parentId || c.parentId === 0),
)

function getChildren(parentId) {
  return categories.value.filter((c) => c.parentId === parentId)
}

async function fetchList() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (filterCategoryId.value) params.categoryId = filterCategoryId.value
    const res = await getProducts(params)
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
    name: row?.name || '',
    description: row?.description || '',
    price: row ? Number(row.price) : 0,
    coverUrl: row?.coverUrl || '',
    categoryId: row?.categoryId || getChildren(parentCategories.value[0]?.id)?.[0]?.id || null,
    status: row?.status ?? 1,
    skuName: '默认规格',
    stock: 100,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editing.value) {
      await updateProduct(editId.value, {
        name: form.name,
        description: form.description,
        price: form.price,
        coverUrl: form.coverUrl,
        categoryId: form.categoryId,
        status: form.status,
      })
      ElMessage.success('更新成功')
    } else {
      const product = await createProduct({
        name: form.name,
        description: form.description,
        price: form.price,
        coverUrl: form.coverUrl,
        categoryId: form.categoryId,
        status: form.status,
      })
      await createSku(product.id, {
        skuName: form.skuName,
        price: form.price,
        stock: form.stock,
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除商品「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteProduct(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

/** 上传商品封面图 */
async function beforeCoverUpload(file) {
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await uploadPic(formData)
    form.coverUrl = res.fileUrl
    ElMessage.success('图片上传成功')
  } catch {
    ElMessage.error('图片上传失败')
  }
  return false
}

onMounted(async () => {
  categories.value = await getCategories()
  fetchList()
})
</script>

<style scoped>
.page {
  padding: 4px;
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.cover-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.cover-row .el-input {
  flex: 1;
}

.cover-preview {
  margin-top: 8px;
  max-width: 120px;
  max-height: 120px;
  border-radius: 4px;
  border: 1px solid #eee;
}
</style>
