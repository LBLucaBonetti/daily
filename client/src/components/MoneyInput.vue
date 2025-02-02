<template>
  <q-input
    input-class="text-1"
    v-model="formattedValue"
    min-height="15rem"
    borderless
    ref="inputRef"
    :error="!!errorMessage"
    :error-message="errorMessage"
  ></q-input>
</template>

<script setup lang="ts">
import { useCurrencyInput } from 'vue-currency-input'
import { computed, watch } from 'vue'
import { useI18n } from 'vue-i18n';

const props = defineProps({
  modelValue: { type: Number, required: true },
  currency: { type: String, required: true },
})

const { t } = useI18n();

const { inputRef, formattedValue, numberValue, setValue, setOptions } = useCurrencyInput({
  currency: props.currency,
})
const errorMessage = computed(() => {
  if (numberValue == null || numberValue.value == null) {
    return undefined;
  }
  return numberValue.value <= 0 ? t('money.input.error') : undefined;}
)

watch(
  () => props.modelValue,
  (value) => {
    setValue(value)
  },
)
watch(
  () => props.currency,
  (currency) => {
    setOptions({ currency })
  },
)
</script>
