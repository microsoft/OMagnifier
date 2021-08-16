# OMagnifier

OMagnifier is an Android APM SDK that can be used to monitor the app performance.

## Features Support
- [x] Frame rate monitor: floating window for showing fps
- [x] Memory usage metrics monitor: monitor and collect the memeory usage metrics
- [ ] Memory usage viewer
- [ ] Battery usage monitor

## APIs
### Frame rate monitor

1. Start frame rate monitor
```kotlin
Magnifier.startMonitorFPS(
    FPSMonitorConfig.Builder(this.application)
        .lowPercentage(40 / 60f)  // show red tips, (2.0f / 3.0f) by default
        .mediumPercentage(50 / 60f) // show yellow tips, (5.0f / 6.0f) by default
        .refreshRate(60f) // defaultDisplay.refreshRate by default
        .build()
)
```

2. Stop frame rate monitor
```kotlin
Magnifier.stopMonitorFPS()
```

### Memory usage monitor

The mectrics we support now:

- `HeapMemoryInfo`: heap memory and vss/pss memory
- `FileDescriptorInfo`: file readlink and max open file count
- `ThreadInfo`: thread count and thread stack trace

1. Start Memory usage metrics monitor

```kotlin
MemoryMonitorConfig.Builder()
    .enableExceedLimitSample(0.8f, // the benchmark for Exceed_Limit type sampler, if we reach out 80% the max, collect the metrics, 0.8f by default
        10000 // the threshold for Exceed_Limit type sampler, 10s by default
    )
    .enableTimingSample(60 * 1000) // threshold for the timing checker, 1 min by default
    .onSampleListener(object : MemoryMonitor.OnSampleListener {
        override fun onSampleHeap(
            heapMemoryInfo: HeapMemoryInfo,
            sampleType: MemoryMonitor.SampleType
        ) {
            Log.d(TAG, "heapMemoryInfo:$heapMemoryInfo,sampleType:$sampleType")
        }

        override fun onSampleFile(
            fileDescriptorInfo: FileDescriptorInfo,
            sampleType: MemoryMonitor.SampleType
        ) {
            Log.d(TAG, "fileDescriptorInfo:${fileDescriptorInfo.fdMaxCount},sampleType:$sampleType")
        }

        override fun onSampleThread(
            threadInfo: ThreadInfo,
            sampleType: MemoryMonitor.SampleType
        ) {
            Log.d(TAG, "threadInfo:${threadInfo.threadsCount},sampleType:$sampleType")
        }
    }).build()
```

2. Collect the memory usage metrics immdiately

```kotlin
Magnifier.dumpMemoryImmediately(object : MemoryMonitor.OnSampleListener {
    override fun onSampleHeap(
        heapMemoryInfo: HeapMemoryInfo,
        sampleType: MemoryMonitor.SampleType
    ) {
        Log.d(TAG, "heapMemoryInfo:$heapMemoryInfo,sampleType:$sampleType")
    }

    override fun onSampleFile(
        fileDescriptorInfo: FileDescriptorInfo,
        sampleType: MemoryMonitor.SampleType
    ) {
        Log.d(TAG, "fileDescriptorInfo:${fileDescriptorInfo.fdMaxCount},sampleType:$sampleType")
    }

    override fun onSampleThread(
        threadInfo: ThreadInfo,
        sampleType: MemoryMonitor.SampleType
    ) {
        Log.d(TAG, "threadInfo:${threadInfo.threadsCount},sampleType:$sampleType")
    }
})
```


3. Stop frame rate monitor

```kotlin
Magnifier.stopMonitorMemory()
```

## Demo

The demo is under Module app.

1. Install the app
2. Run the app
3. Click the button for testing



## Contributing

This project welcomes contributions and suggestions. Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## [Security reporting](SECURITY.md)

## [Code of conduct](CODE_OF_CONDUCT.md)

## License

Copyright (c) Microsoft Corporation. All rights reserved.

Licensed under the [MIT](LICENSE) license.
