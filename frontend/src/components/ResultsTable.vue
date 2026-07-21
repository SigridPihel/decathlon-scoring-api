<script setup lang="ts">

import type {Result} from "../types"

defineProps<{ results: Result[] }>()

</script>

<template>
  <table>
    <thead>
    <tr>
      <td></td>
      <th>Athlete Name</th>
      <th>Event</th>
      <th>Score</th>
      <th>Points</th>
      <th>Date</th>
    </tr>
    </thead>

    <tbody>
      <tr
          v-for="(result, index) in results"
          :key="result.id"
      >
        <td>{{ index + 1 }}</td>
        <td>{{ result.athleteName }}</td>
        <td>{{ result.event }}</td>
        <td>{{ result.performanceValue }} {{ result.unit }}</td>
        <td>{{ result.points }}</td>
        <td>{{ result.resultDate }}</td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped></style>