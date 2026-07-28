<script setup lang="ts">

import type {Event, Result} from "../types"
import {computed} from "vue";

const props = defineProps<{ results: Result[]; events: Event[] }>()

const eventLabels = computed(() => {
      const transform = (acc: Record<string, string>, currentValue: Event) => {
        acc[currentValue.event] = currentValue.displayName
        return acc
      }
      return props.events.reduce(transform, {})
    }
)
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
        <td>{{ eventLabels[result.event] ? eventLabels[result.event] : result.event }}</td>
        <td>{{ result.performanceValue }} {{ result.unit }}</td>
        <td>{{ result.points }}</td>
        <td>{{ result.resultDate }}</td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
th, td {
  padding: 0.5rem 1rem;
}

table {
  margin: 0 auto;
}
</style>