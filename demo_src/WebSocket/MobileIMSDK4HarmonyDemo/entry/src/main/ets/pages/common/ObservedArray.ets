/**
 * Wraps an array as an observed object
 *
 * @author huawei
 */
@Observed
export class ObservedArray<T> extends Array<T> {
  constructor(args?: T[]) {
    if (args instanceof Array) {
      super(...args);
    } else {
      super();
    }
  }
}
