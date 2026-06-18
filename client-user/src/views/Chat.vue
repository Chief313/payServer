<template>
  <div class="chat-page">
    <h2 class="page-title">AI 智能客服</h2>
    <div class="chat-container">
      <div class="messages" ref="messagesRef">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['message', msg.role]">
          <div class="bubble">{{ msg.content }}</div>
        </div>
        <div v-if="sending" class="message assistant">
          <div class="bubble typing">正在输入...</div>
        </div>
      </div>
      <div class="input-area">
        <input
          v-model="input"
          placeholder="请输入您的问题..."
          @keyup.enter="sendMessage"
          :disabled="sending"
        />
        <button @click="sendMessage" :disabled="sending || !input.trim()">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { sendChat } from '../api/chat'

const messages = ref([
  { role: 'assistant', content: '您好！我是 PayServer 智能客服，有什么可以帮您的吗？' },
])
const input = ref('')
const sending = ref(false)
const sessionId = ref(null)
const messagesRef = ref(null)

async function sendMessage() {
  const text = input.value.trim()
  if (!text || sending.value) return

  messages.value.push({ role: 'user', content: text })
  input.value = ''
  sending.value = true
  scrollToBottom()

  try {
    const res = await sendChat({ sessionId: sessionId.value, message: text })
    sessionId.value = res.sessionId
    messages.value.push({ role: 'assistant', content: res.reply })
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '抱歉，服务暂时不可用，请稍后再试。' })
  } finally {
    sending.value = false
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

onMounted(scrollToBottom)
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 22px;
}

.chat-container {
  background: #fff;
  border-radius: 8px;
  height: calc(100vh - 200px);
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  margin-bottom: 16px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.bubble {
  max-width: 70%;
  padding: 10px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
}

.message.user .bubble {
  background: #e4393c;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message.assistant .bubble {
  background: #f0f0f0;
  color: #333;
  border-bottom-left-radius: 4px;
}

.typing {
  color: #999;
}

.input-area {
  display: flex;
  padding: 16px;
  border-top: 1px solid #eee;
  gap: 12px;
}

.input-area input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
}

.input-area button {
  padding: 10px 24px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
}

.input-area button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
