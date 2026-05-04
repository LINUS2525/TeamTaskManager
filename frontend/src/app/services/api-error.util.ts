import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../models/api.models';

export function extractApiError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as ApiError | undefined;
    if (body?.validationErrors) {
      return Object.values(body.validationErrors).join(' ');
    }
    if (body?.message) {
      return body.message;
    }
    return error.message || 'Request failed';
  }

  return 'Something went wrong';
}
