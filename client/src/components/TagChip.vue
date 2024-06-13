<template>
  <transition
    appear
    enter-active-class="animated fadeIn"
    leave-active-class="animated fadeOut"
    ><q-chip
      removable
      class="q-ma-none non-selectable"
      :style="style"
      @remove="$emit('remove')"
      clickable
      @click="$emit('touch')"
      >{{ tag.name }}</q-chip
    ></transition
  >
</template>
<script setup lang="ts">
import { QChip } from 'quasar';
import TagDto from 'src/interfaces/TagDto';
import { onMounted, PropType, ref } from 'vue';

const props = defineProps({
  tag: { type: Object as PropType<TagDto>, required: true },
});
defineEmits(['remove', 'touch']);

const style = ref('');

onMounted(() => {
  style.value = 'background-color: ' + props.tag.colorHex;
});
</script>
