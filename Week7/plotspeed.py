import matplotlib.pyplot as plt
import numpy as np
import io

def plot_speedup_and_efficiency(data_string):
    # Load data from string using numpy and io
    threads, time = np.loadtxt(io.StringIO(data_string), delimiter=' ', unpack=True)
    # Group by threads and take minimum time for each group
    unique_threads = np.unique(threads)
    min_time = [np.min(time[threads == t]) for t in unique_threads]
    print(min_time)
    # Calculate speedup and efficiency
    speedup = min_time[0] / min_time
    efficiency = speedup / unique_threads
    # Plot speedup and efficiency on the same graph
    plt.plot(unique_threads, efficiency, label='Efficiency')
    plt.plot(unique_threads, speedup, label='Speedup')
    plt.xlabel('Threads')
    plt.xticks([1,2,4,6,8,10,12,14,16])
    plt.axhline(y=1)
    plt.axhline(y=0)
    plt.ylabel('Efficiency / Speedup')
    plt.title('Speedup and Efficiency for horizontal contiguous blocks')
    plt.grid(True)
    plt.legend()
    plt.show()

# Example usage
data_string = """
1 82181
1 76899
1 77521
2 53584
2 54374
2 52939
4 32766
4 33486
4 33615
6 29083
6 29155
6 28034
8 21258
8 22953
8 26054
10 25151
10 20297
10 19996
12 19838
12 23680
12 18804
14 23645
14 18726
14 22764
16 23818
16 19505
16 23312
""" # Cyclic

data_string = """
1 72158
1 74055
1 73934
2 28688
2 29535
2 33154
2 26721
4 22808
4 23477
4 23340
6 20140
6 22211
6 21288
8 19459
8 16719
8 21552
10 18006
10 18148
10 19838
10 13028
12 15969
12 16854
12 19525
12 15681
14 16939
14 12351
14 14841
16 15874
16 18008
16 16682
""" # Hoz

data_string = """
1 31768
1 31478
1 33934
2 22000
2 20366
2 20884
4 16307
4 16320
4 16646
6 15919
6 15397
6 
8 
8 
8 
10 
10 
10 
12 
12 
12 
14 
14 
14 
16 
16 
16 
"""

plot_speedup_and_efficiency(data_string)