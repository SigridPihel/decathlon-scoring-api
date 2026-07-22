<script setup lang="ts">

import { ref, onMounted } from 'vue'
import type { Result } from "./types"
import ResultsTable from "@/components/ResultsTable.vue";
import axios from "axios";

const showForm = ref(false)
const results = ref<Result[]>([])

function toggleForm() {
  showForm.value = !showForm.value
}

onMounted(async() => {
  const response = await axios.get('http://localhost:8080/api/decathlon-results')
  results.value = response.data
})

</script>

<template>

  <div class="container">
    <h1>Decathlon Results</h1>
    <button @click="toggleForm" >Add result</button>

    <div class="content">
      <ResultsTable :results="results"/>
    </div>
  </div>

  <div v-if="showForm">
    Form goes here
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
