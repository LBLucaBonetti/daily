export function validateNote(note: string, translatedInvalidMessage: string) {
  return !note ||
    note === '' ||
    note.replaceAll(' ', '').replaceAll('\n', '') === ''
    ? translatedInvalidMessage
    : true;
}
