export function validateNote(note: string) {
  return !note ||
    note === '' ||
    note.replaceAll(' ', '').replaceAll('\n', '') === ''
    ? 'The note cannot be empty'
    : true;
}
