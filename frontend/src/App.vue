<script setup lang="ts">

import { ref, onMounted } from 'vue'
import type {Event, NewResult, Result} from "./types"
import ResultsTable from "@/components/ResultsTable.vue";
import axios from "axios";
import ResultForm from "@/components/ResultForm.vue";

const showForm = ref(false)
const results = ref<Result[]>([])
const events = ref<Event[]>([])

function toggleForm() {
  showForm.value = !showForm.value
}

async function handleCreateResult(payload: NewResult) {
  try {
    const createdResponse = await axios.post('http://localhost:8080/api/decathlon-results', payload)
    results.value.push(createdResponse.data)
    showForm.value = !showForm.value
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.log(error.response?.data?.message)
    }
  }
}

onMounted(async() => {
  const [resultsResponse, eventsResponse] = await
      Promise.all([axios.get('http://localhost:8080/api/decathlon-results'), axios.get('http://localhost:8080/api/decathlon-events')])
  results.value = resultsResponse.data
  events.value = eventsResponse.data
})

</script>

<template>

  <div class="container">
    <h1>Decathlon Results</h1>
    <button @click="toggleForm" >Add result</button>

    <div v-if="showForm">
      <ResultForm :events="events" @createResult="handleCreateResult" />
    </div>

    <div class="content">
      <ResultsTable :events="events" :results="results"/>
    </div>
  </div>

</template>

<style scoped>
.container {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
}

h1 {
  grid-column: 2;
  grid-row: 1;
  text-align: center;
}

button {
  grid-column: 3;
  justify-self: right;
  grid-row: 1
}

.content {
  grid-column: 2;
  grid-row: 2;
}

</style>
