Tests will need testbase module, which depends on core.
Having tests here would introduce cyclic dependency.
-->  Tests must be in a separate module.