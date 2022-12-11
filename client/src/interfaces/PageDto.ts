export default interface PageDto<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
  numberOfElements: number;
}
