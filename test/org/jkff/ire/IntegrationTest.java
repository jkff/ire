package org.jkff.ire;

import org.jkff.ire.regex.RegexCompiler;
import org.junit.Ignore;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static junit.framework.Assert.assertEquals;

/**
 * Created on: 04.09.2010 18:10:51
 */
public class IntegrationTest {
    private static final String DNA;

    static {
        String z =
                "H4sIAAAAAAAAAFWcbZZjSYpE18rhBxtg/6dS3Gv+onpmeqojM6QndzDsA3VN/f6np3bq3792//1f" +
                        "z/a///fvp70z//6suv79U83vX9X9+8N/f9T77397/v3l/f3qvz/+/Xx/vzz3UnP/uL8X+/en+3uf" +
                        "f3+9fr/47/fn98b1e61/f+H3n/7927+/8+/f9vc2vwfY/v3Vfy//7y1//7P3678fzb+n+L37vz/4" +
                        "98h7//d769/Pfn/x99L+cO+Nf6/1e9rfT/495X3s31P8e6J7z3v+3y/+/uLvrX/P+nvc+X3w/T36" +
                        "+ql+D7DlY/IwvOTvp/8e+Peo//7q7y3u4X9/9u+Hv9f8/SUejCP8vfPvhH/H9Pu13/P9juR3FL9/" +
                        "7nukvb+0dw33PHc4/TuXu6y68yyehV9czuBeepfDaC6Ct79X3fH0ePbKA/0Oo+66/a25c947prrn" +
                        "rrv736f9ncTeefLZ72z6Xrbvn37/gQv9/affLd2J3lX8Puc96q9UrtruyfqO7leTfef0O5G9h6KO" +
                        "lvO8S5r7K5zz/d7cK92d/D4EpXYHemXze4y6+uEyqil2H96qpS55gPW0fy98T3tHfSfzO7PrjH9/" +
                        "b+iJX21tU+Z91XIVd+/7++kdPv32+0jXev17nd9B/g79zsO/xHn93p9GmqvCO7XfL3FZVzv3J/cb" +
                        "1zZ30b9PQTVfMdFDU5zM76PQ0VUUCe+wa/P2Pdw1zu+IxqK4xt07UZ5l7uLvQr3z36H8uu1q8wpk" +
                        "eK2ihA4gRBra9oDgjrrux4cH95z3qe4Afq98tdPUVqcgV0i50uAiryOWj1K8wa9Win6s+//XDT0W" +
                        "1FJE0xzGHfHByV4pDv001kpzFocEvwquoupoquvVtjVsB971PpFtR23ScPTy7//TEV1lcR6o3MVa" +
                        "lDN3G1eLV34CYNlSPOK9ynXR4UMfct8xvZc+mLDneFg67FdKV+VXW331svRiC/sHjRT3wRFNcHjr" +
                        "JxArfxXGVXW9thlqCZBcar8sPNrdMxi7kQoCEvrQdQSxAWiWl14671oGrCt/g17jL92R3g+uPZoy" +
                        "PSi94qNwr6IPJPp+JL6t5TfrcKTnr5c4h8PFuculTa5brpBpr23P81qxKT6KPijA4/BmZZEHxm/A" +
                        "Lk/8ezO6pq8aLQRemDe/36RfrkTuztoBuTTPvcW9/lzRX23OdQBPBKTcyd+wAn/yB3PX/AOE8X65" +
                        "Su4+xKAASB7joPtatEEoGhseQtc4SSiOm/Qcy5W8jOWg5wqmmRIPLBinEAc/7MFre+y2gbcLih7w" +
                        "BxEX4sIcSz0Op9pwhaFbPOldf5GRAhOBYLQfNHczYgbodMPo6EN7dtOe3h3XMPdsw3u5hRy9Gudn" +
                        "Rfdfhx5wH0EZYcQi511lNF5uqoeLGcqnAnW/Qj4CwBMFQATzDVpeed+HvhbdzNJN1Qao7r3hWQy5" +
                        "g2aq7fjEkR36mhu7Fzxac5/2KsqxyiidO/JJ9d8/D2ANDoM+j5I5IW84U0O1DyHuLmCJPnQmlSyn" +
                        "g+QDrrfsDybYnIHE+uqT6i/pYMuH6b4ZL59/s/3vWe6XZVFwgrvtbkc0/Od+6w6F2XhEB17Tcqm6" +
                        "Qrwr4vRv9tyTO8Sc/i2ei0ItrkYY/H40lcq3OCvcC6YzgvPdOSPi6Fg7crsdhj6rvG59Skb5ykX4" +
                        "VMDMNQSI4tExEdQFc5U5tMmVE1dK1VlVdMaR0puGYDNTlUZixKAxFrrhQ8HPmxb08O5uDxuOsvwQ" +
                        "5KZJc7JORsBuD1kss/uTI2Q8tw/JcOSloSQ3P6C95WjvjxnBzCnDEXK5nT6CsOqtCSsONC+FY48J" +
                        "RFdph6YFDwfpQVxH59h1987otnl1NZzdFcndzFjpDNurqGa4jqSp6dGW0x8vt0buaKZlJ7DYd6BM" +
                        "U4GMprtrOIbTTjHahw+O9HJWSFTL1wg9KJ/rlEloy/3FUHtkweH/9To6hgGynqHjtmzZ+1vAHrOK" +
                        "V+fuafv77HwqmDKC1T4EQyFvkLRmKMBW4TMH+Pda7QRoqXcYRYWWdZWjN/cMkb9JSAv79E3H0Ks2" +
                        "wQa0mdMgJPMfslDQjMpvqGYGnKhoqs1Ms7c5XCQ4dPfmSlPGaGME93FiUHE0CRCN0aI3IqdDBdSO" +
                        "tNb1jfwcVce0w0kYLAoJFbV4XdJv6FGCD92OyjK4nJxHGuduDGa16blj0HCT6ejAeQRE8KWXx1o/" +
                        "kgw1gwxWJN1gm6hbShbBfAGkqTa0XTjjCPh0402+mCi/M7tfsgAVciK9egHCtnBh3o+Bca9L5cA9" +
                        "qL9SfBf0rjV4FvXqzR+THQVO6fE44ujg/Uj+SSYowCAxK6oqSA5za6d7Wxye8nGsZmSWndp2KQAF" +
                        "3Wo5DF6VjLnGwlPoP69oMakAcbgwF7teH8jvxGReA1zxBGhLal+vhYrAgkPwZYLRxFcUEJmr20GX" +
                        "VT0PYnXsuMVVEqzNIiOHyICTHDLMEHjOqL2hAtu5oznh3OrcmedqoSJQcoEeuSuqZJyQqNNyRD3j" +
                        "brm/o338a3fDcqifVoStHfG5LUqXhxyvUjCA6FVOGgaEJEVKraaQUJFp+zEUrT/AopAuEl29HkCr" +
                        "goKWHsAzn9LVSIJo3NnYuc0gkwuUYgqtifHWfkoLb1WC6FWdoVVylO6ECgu4HGePFpt3tv2422Zk" +
                        "r3g/pc1LH8Cfj1HeWTgLUWXzp1nRBlgsJYzGKLrrHfgPvSS4McdUNmoMGVNoCsjRjjobiz8bHaPV" +
                        "k4jptDqO5dy5z8DcLW5BXTwcxEPtUSv7aSF1MCMOqZWtIAeVORU/YTAC9j3tcG53Ir3PEaUz1Ec+" +
                        "kcNrQn44/rAEWnsEuA6MUeubcbuCNy7o9Y5eTrwTZkClTuxc+3ajOdbDXAkENjSPjujHyCvkaxQh" +
                        "7vMz6tpk4S4ewH1MRdwBn0oq15LTMhzQBOrUKVY5xJJ2fpZopw1xzxDicBSNp5Hk+zYd2yPve/7P" +
                        "tX/rCirHytgA1oNBCoUbXTD6XBOhPxbIiIssP77eutVt1wFVf16J1KPwolD991CL8G3MLsoAGsY1" +
                        "l523uuRmEYy66/coIR4caqlFIwtv2cbaLvdXNtd1RbU4J9s5+1bd8TE6mrU9xIIHUOjxpUoXfiRf" +
                        "pXFnr88fR4cLbC3dKCr82DerWg3vHBynaW1FM0X4qVxB8pZgXOvRbHrNiw2pBjXy6Jx9/AOFv66t" +
                        "Ek9yljO7p78pBgYY8UhUJdfzofNWiJT9t7AvFMwwb6hmqM7rMdR1gOtjJ2gyB+rIiQrJW1bMVizk" +
                        "lpSqu6cc6ldu+9BQOsFVDrbbBXRPYTcNsj6irw8Sl+yhbW5lshKfAtj4sW9mCjtRnYkWiK5kyvvp" +
                        "0HoKOLKldbNQd6jqzoe42+16PeKb0lgR3LGPqIuOptxPxCPn5BGYS2jUj3E3zv34mRGrCi2IZ5da" +
                        "JXfo5IEM4WhBf3Ik87EZ7oHIdPfZiiZzUNDDWwcGWhxxx4VNroLbzXBYxvOaBKGxbtqhoiushk9x" +
                        "QMxkRbsES57i32j0YH3lXT0nhBxiCIJCBtLRoSG4eipthgdTrM/TnVE8IaIkARKnkslW+jjkVrZq" +
                        "Lfi4pgU94RYaLk5tSXFrrwjMrVOLd/mFVt6iZJHmT8KMpwD4yWY0W7ffJ5jcjCGLqUM54Xmt2Esa" +
                        "1zgtJcw3PBBYbh3Q+Rj6rnIUyRejs0BmsgHOTtEkORI5MxW6MgOxmIE5uJMQiuz+UskRiTL7wz6F" +
                        "Lvp0WvZrItGjd4Zw08mzH3GPfBeRs3BLHxM3+2nN7XEOMqg/Rwqz3UQYbK5JIN9agxtVzrhg5I9q" +
                        "GeRnSD/Xvpjjf5BsmNcItlYgasdANkcNikSDozyKhbKRSY7ejeK6/kcljftG5LBQGC26cr53iVik" +
                        "NfeuhkD7HBo6626k/dDrcXWozJ3nRrfhRZHHUSVw2TtuoU+lRfs9o7DVonKR7ohAiqtXJh6kRFUw" +
                        "6WjRTWZVI1HFixxSF/C2tdy3HNYD5WqfksHWkMUMabwVTx/BzWTmecq0gwZZ/S4XKQbinc6gNeKV" +
                        "xFAYxsYbMsk2hFls8LFmdz6GJfrw+msUZQSJVIcYzwvx+xvG+EhQD6CnFeO1fxIYhr0OkC75Ps+D" +
                        "DOfuwtp1wJAoLT4zwV/ACzG/Qa3hSaZVPNr2qmi8e8+ZKFNPupR+t9/QkyzOCa5X4iMgALCG6QJA" +
                        "wp+ty0lJZ7kiVLd39AiAtlOsCCjnluccZU/2TC7dyXpbfw/RA+MAlHWHFoOGibY07oiVPASUXAYn" +
                        "a3fwQ98AJRxErA2rkleXU2qowa+KG0ZzFswJcY7ER1GtfpczXsRdE54prSDN/W9vyiZ73tuGZ9Lr" +
                        "JvlYOUcvchabOA2krz8rAmZXBNtvAWTMoOr7DakymNvtbhMkadLbdxEsTnmktFn7JrTHPCkL39SG" +
                        "eNYzrxPJVZ/r5/ADtuTnivfBcwSDqR3WgqjFg/IY9EmuZG+a6FcsCAk/wEkVnCSFtZnGOq1XZCKd" +
                        "0Kg0jh4NCMplovKsCM2+ERQcPPSjyyvAWj/fbZDRrVj0w0PPlAG+/mie9B90jj83JA5b4RWWrXtN" +
                        "93IY+4FJlEdDzEawEVdb46KdO+1KUTJS7Mylg2flJ/s+79XUZwFM2Bfl17q3qFMUdutjPiGQvy3p" +
                        "HEVSG7AoEEqpGo0OF0k+LD3ACIWrVlJtm9y5jdgGpdmeZMy9i6qxqJWhFVcWzge6rRH8kvAdHebd" +
                        "YCgRCOYIK6/eidCVEkR5MAQr7tI4MXffjbl6uGoBTSMZRDv9eVV4wH0y4liNUkcfCPHoKKYneiE1" +
                        "t4YxBsayZ0jisITikMsUgGpAObfCBUCOyg4BE8aHuNncWEr1SnA8HfZ3RrddFrWK4x6jdrwbG7MF" +
                        "KUE75vXKidx6qSjdzVx71GEqHJb01IEol2QJZvx4Kp6RPTupKDrkEvKwVwNzjTEYk0/JxSKdSTzB" +
                        "Ao/9hFmWCVYMZyyb9jxF0DUSepBV6k0qboQ2JIRbaM/BzdS5Qc5y0Iazxrsp10JWbwLa6Rk0XAwJ" +
                        "yobRSgDnuVzGALxZMzRLFnWS5yjealVQlGtEt4Y+HU9LHcoIwXmj1dTES3Tn5gIs5G6sbAEJxEDc" +
                        "mppyQlklloftq/HyiT21CG4n1QDioUsMn6QcsK7YlhgY8AG4yE6IDj3X/uM8x/djsVFgWhl6bjB8" +
                        "RJ2G9USjAuntKiYRjE5kRzxBhewXleuE4tHODJeByxizbzT2aG7NEyvaubTCS1/w8Oy981eUY0xq" +
                        "hu1L2TmemC7SCvp8Ql9cr2JfBH6NuwXzZT2jabZkv6sH4Kz70hQ10/3NGIxhtoy5gSvjcUCWupMY" +
                        "qxbD7fbbx563O6CDUtIINdC6lds0f+MzTz+O7uhBQmgJYHu0aR9mThNCsP0z4eLAoEEDRkd5Jich" +
                        "MAxb72/cL+poa2RNHB3kvByt8lFQwOUY7nifNz07nk5XvOJeowjQgCvqcaH1IesKtiba5YnKT3Ju" +
                        "EQhXCnwshl5tThK/WfbgpIMjbtxMm+lqwu8qCGTTKXyJCt2lWxWRQMOsUz4p1sadR+ZL3EYnX5Yo" +
                        "HJe0AptkWHzCEWH+i+u+jt3CDERh94QK/ZnuUdij+3WXX9KpVd8q0HHSRLbr+NIVQKTRzJmZ6mJE" +
                        "ncqgmGjAUMRPd1CMtiiNQVY4TBpB3IklOkYMI2BInfZb/NGRHsrJpR81ZcWJnH5pVvQIT70ZlPzL" +
                        "EGpDoSe7mBXOJYggYszg6OpKlgZNLqVKh6cz0B6TEsMeAR9WCzRv2tWm8YsSbBGN0rQdZFl5eESD" +
                        "qnrFZdczeRsN6WjwBkRhXZAvxytfZU0jJBVIkA3XZLKO5i4vUhv0PMLnY7XfdIHE6AnGJxq2VpBz" +
                        "rIfpteHvCUG8REU9wTerrNnsPuh6jxR8s/YAbDvmjCPsCu+OI1uNWSSgxjByR+EmvIMd/QeYd6Nb" +
                        "ObWtyJ2/o1osS5l/zHQyG3TDiX22DMQWBXzHlgTqfumdA5wgYnqNEffR+qw3IW58WjrIEFrXcSox" +
                        "1Bi0wD4NfrmNNShYz6IVOJ61rHT1bxi9x/nksKvJlU7cB2uDT+zSnkwcYoGp85EO7GOdmc3Dtw4G" +
                        "2DxhXVBXDeKeLM3qrk7U8mK7RQ0iXvbbx44N4OgLW0t2Nb1xrKBlpmPEf7q4Tcxt/LW210NuNCWM" +
                        "L2AdIYBpDhN9bEr9DFSNZu5oD5eCq+VmUBVd5sD3xktYRoPTkXryJVRs+F1GJ7KysZ00eE3wNjG1" +
                        "/pJ+aeUO3ddwZa0iZu7SFFfs+tBM2keS4rskk7fVfS3Ddk/qdR7cBwmcOhkVnjIFuGWo8r0YHISC" +
                        "h3dAkmxvJBVmCMrNj3hSIRNfZY3xMeAZepVFgk9gzB9qggyF0GF+81LwTVtH1oqNsjGswehFRr5U" +
                        "YzU628bMSXD7Sj40r7LF9T6mPsMRL2Tddh0PZd4+9sTGMR5I3+GS8d0IkguOLgOkwqm1J6KyaCyg" +
                        "DUDsJAIrt6MjWwYq39tYuTk50bYTJmHDyNUURMhoAZ9XowJ7n3nz5x302mCq8uCN6ojDEeX6cK7C" +
                        "rje7jIwWnlGLBcnj4NtsNEdJXasbnsfxivf5KOmqmbJw2grWkTqXehb2tSIuCePEaTODmsnSLm4o" +
                        "gQI26V1ovjbA1d25H68HTnn6uxApfGoQ9jt6DEY64WCY53fcjMLvnTebKXhgMA85YUPNdXMc7rJw" +
                        "QsfGO3FLqRxZ+evILVxZl6iYyRvrc418oMfM8tCrTfisUq6OKU6lYmm0xh3ej3ohJ5dKuvtV3Epa" +
                        "pXSRCNVRHZzBMP92QtETSLcfFG/ArQy5UomSSpp2zd7dVZ4nFBJth/kX9qBMq4Q24B3CikU2jRUE" +
                        "gQwR7owr6FdwwXK6m7AIvrf6HpCbxs1wFP3Zx6aNnNdICg4n9upu1g6yIRgtLpkck81F9Cj8Kl4d" +
                        "myH4ivgD8kb6107dT2kttbsO8A3WkHjnKu9FEXr2aGdiM6J1QCe0ZjVesHYEa+j0my4Qi/E4KUU8" +
                        "DCbANUKbnIL1+B/oaDlpK52s2XFTQ5bmUhkAZ7PAuEUDrtWAayI5649rDG28fyK8Yvobc+JqMRGi" +
                        "CWSgGQLTLwUpo7fIq4Kmwqxp+a4ksbJGAyF6T1dFQ1hNVmyeigLoWufKhEwX+yF+NnRn+wUHN2pH" +
                        "UsgJa99obxrJl6agsGz2kinFWcTp9KzvtphrclNtjnbDojqWPBJ8Os79O5hn68Dwt156bSTMnaub" +
                        "HjWY3CNl0Bwp/lvnIdT03zgjEjR9rHwG3wRmPW4Errdrl0bceAsQHDjaxMENM10uNyfdb0W3dGCs" +
                        "s4RlE7aD+svDQtoAfb3U3ihjv1054MKGfCVGgvPpqOHl4krQvq417lMSPcmbx4UsbEOqJ9bHRTqs" +
                        "KOls1dvHZhHCmoKzls60yFOl1uEgblOAHYXPyY42cEBDVRQqsq6VmLYdblFP/IoN3ajW0o3H/CSt" +
                        "LHa1p9/OnD9NdyRNRLyjnGSUpYaryjZDuypneGG/0KSmRxTVhku1OreM5DWOkMI6JdhndlFc3N3A" +
                        "DgiPy8xlf1Pt6J8pAlngQ41WsY3ZajO5pYFYsaxJcZc65W4ZPaRxYhxjBFkncjaa7Gjfth7h6lH5" +
                        "HpV0S5nU+sMmxszf9Vebytcdxtf1f3VM1UD17WMjkiTQbXpe4psmPHQVudAGFRoRr16UrzLJVTvJ" +
                        "RJVR8S1pXUwi/0RzpDRiO44OasuUqOo5IUwqpTMM8qAZtGITUCpWag+FJAQJL5YyTh6AkeoMWEdn" +
                        "tgoV3Qmz7zHBm5pYTeIErFen+ngX8KzrV6aDOkKQa7ciwk1zOcY0hAb8ZDczeed/+9jlyeWoIiBe" +
                        "M8WbawOzka4B+JBUaaAcUPiEIOu8Ol+oH4u6Naelt6vCViY4FvYZFVcu4/9rFi7u+ADpLS05nOpU" +
                        "oxEeuqEr3kq9FaRAu/aeeoQF00ce2qHCpI6nQ6SS5CUWoP04vhXlm+/SX6NDYdeFFB07cmJ92tWR" +
                        "FF1bZdnJ0BiuLwGAtsNE/0AVybOTpxF/TUqoVoccOtax6N0S7PzgMQKt3cIFrnIjAAtt9Jfmz4gN" +
                        "s9iSlytsyYvXe91M3nwbZHjSBIQuTMneWlqsUsNASlYhiLcfJG1WujOWLPYtByBUPCjH5SHbg5rV" +
                        "d5VAKiTM0tkS653/s1EN5JYb64YTWy9ZgkLPlHr2sSITNkT+M8R1Is2juUW20156V9mRQVqwUwkG" +
                        "MfkambxZmQqgtKroTwebVIQt4b+ZUPGSVJ1HP/7NVW0BAh1A2ucFoH06Sus+UnJQ0gOcEk2jCqvL" +
                        "NJfQMrVuAi7R35C03KdoL9G3MqUY9abT5Zq4NCKtOQC8BQCUk4YXnG64/u+J2g0WpsiffWzqDrb9" +
                        "bT5ONDGUh6TtTgN6fPggF0PTO2R0zWCc5XUx4jH9OD1mobr1gsY4xHD9suVHhOBSjquw+wLQWXrE" +
                        "JefeYN0GuTvo7XztP/vYvn4sSewa9LMx8sjS3KgRrsVQZBPrQl8Qywsx04rorCv72E4puZSrRwvc" +
                        "jh7TlZJJV9oe5ktNcZaFJd31pAsuA35I0V2Gkmy6OiFik3i1VohoYPVsOEJ6iZGhH+E+NrEkg6t1" +
                        "XBShjIY2or0PFdNdVsLwIfXQpY0ud6jr/I97m2TkPJo0y+wBxjQ45y5jsIYx6VZDohjETw8pFBu6" +
                        "1+4QLRDR0vuQMbMRXb162WY+7f7Zx16NwDJnt+N5xsDcsLBy9xJny/1kveneZL+tPupYm2bJB1au" +
                        "zx7bj4xGGXl8/pqW3v7ZSolZ7u/5v8jqjrUz8RU7lOZP8pM0zvQrCn4RnI8RY88g1v1seCWOI7m/" +
                        "x1aCmlt9OhwGLpXWlFZuJu8YqspTMQ0rmBi7S/amosAHmxhMQf2KukEqw5SJYEIrIIPAUqaR4xO4" +
                        "qhiV/9/HLlUTlVwg31vNiUnaYkw/6HxZQaSbcixnlaUorSmkx4wBl/7k/UWrdiTMWI3snCLG21xY" +
                        "GDcIE8uLwHajEe+sN/vY6zplqkLe0OaehDzrUgg4BvvVpZZubIZCS0kkxXi1UZXmjBLefmKC8s3T" +
                        "tpNT46e+fWwOl762bsbYiC8W5qBdo4ne1sLGsfHjrUUzL2myeDIjPb2AQtIbMu0C9GM6IRLeKokx" +
                        "Nw6bfPhjiJbIRC0iDHB4HtuDOJmPg1XuY4PSZUKNv3Ie3Ka03AuaPIqdVIxbpb+fmsZu81mAvzUR" +
                        "XPj6mqaisYaxXtG068ChnsVBH57ZNo7h1vHmBud7vP2odibzp36b1ER3xqqLE29ZWT0TsV+xKUDA" +
                        "8ntM2cvbVJE2mXsgkwXKlTt2ANPUEyu11N2gjmrFqxwV7WDpyRRWf6nFc+VB+4HtasAikCzY4bBa" +
                        "3i1t9ezSOohL3cFsX41JJr+0+kuynHy7Q1fFpAbKCcefnMQNXlq6VZv2xpea6A+vphqEttyleDjj" +
                        "m761vzULVwOt9irP094Np6yLVJhNrymZa3d070vkpqetPVX6+fnyvi8vW203NSC+x3BKNEEmKo9k" +
                        "pxSBgsAe0vfjNCmAO2keo9wyV8wrDiyTFrilWIwDb1c+qcfQn82AtfumMs/q1Ll+PKY6WgcR17ir" +
                        "WWrR+IjnEua+lj6LgqUn4U6fmRyDc7R0IYzzVlu6lad/1EsZasMg9B4IPeMRwKU2yHAtUJ2foy2f" +
                        "7eoxrfa/lqsLQOQHxW4cbVsr05vI35JtIN/QKERq5bbcS1m4jj8JJxt5TWkAyFigfqOLT5txO5rJ" +
                        "OFHicGJLveGh281OWgcCFgX+5CoNmKKQ15WQmA1F841aXSbI3ilOjfIaw0yJHUo78dhkIl8XrR4J" +
                        "NGcfAo2k8nMK79EfPdKDQQZc76vYNAtQd4PK22hTgZ2+XdPgsIiQm9J+H41/NzgQeUa+Y2Zf9SZS" +
                        "B4qMCcHb9bdgveQjZgI8UkJc13queD7HVF+TjNtv1+Nllx55Saj/jB9RETHX8pM1X+uIr3zrT4Gt" +
                        "3y7nvqfhdq6712+80fIMV50eNeJzdFyDiLn04qEMPGcVnXglAYCUE7jCTo8cP8u+0O8aLQ4ZPnoF" +
                        "dldbhFnH5EaUDXbKiwocqbohqXygriSL5DhQHBdbTBf8xqLobHZVfiabKrZiOF1lDWV0HXEE1xRW" +
                        "ysdY0LmJubVxUcU3KsuLles5RxBcqAgq1I1Ulijb6mMgvgzjMEPG0c9PM2OgFoNyTC9Ps0gs+bys" +
                        "Sm48GzZOeHU3lEintLQmjOy5M1pwKmCce20UF1JyFcSDkJtMgCV8KfNZ2KSiko/hpt5mGHCNPGVc" +
                        "l2/1HgLcVumyPOIOupKBmN9pnd9gsK2SXuArB4WBMItvaQfsV7NM3hNbJph0xCPbHhVuNZ0dXDcy" +
                        "JJdxhiNbmOVSyXnTTsoA7oup9qpHgOrGUEByaZ/gIMFokgvcFW8K/phCu74ORq9uCfUTl7LiIhXi" +
                        "apMIlVS3xy2KVBCb56a08oFtiTAxEQShIOUwOuaHdjZ6pPYZV3hPMZw0xbicTQjWGnZOjJKMRDCu" +
                        "rTSarzhhrUqJz6yZglI7irv7+FA6uNZjMl+rSkJyt1X9LJY1ENvSDykHpdPCtDujZGJlnRTrlBiR" +
                        "XbwSPr4JFy3NNOdNOln1RwWF0Yp1N3GS4WoxwJ+7/bFSGWappK+g75W6Y7f7LyokyMkTecVWoyey" +
                        "9ViaALfo/H3uXl5LOrvZC40SxcUobYCOa7a4AcyNwdWybb0PEVQK6/6jc8HtPYUnhBseOSO/28kp" +
                        "IPf/OENx1uW+MkHt9PBGFUpFCq8Wso6jTASX7Ntg4gZPSUsO5XvlvXCiYLwIIVtxONyRsDT2uO0J" +
                        "xbU0RrBv1/VUTvy0XlwMuOKtILaLObCx/Sqamw0R3F2/mbRa2cwLeYuY1HpVC6S8wn9LHvqwh/H8" +
                        "0/vKCmgez3jkXnmwiRvMQ2tfPpGkMkQkg4ZouMqHj1eerOuwONyRqfUxLJl7+a3jeoE4LvYobZiE" +
                        "TNqS0OoEQIbm1eghrtPUzUFSRqmcSsLrcAowPHqSnBThiNoU/DGNdsqu4l5TayIarNvaGABsIYym" +
                        "tA68ztq3uCh52qCPlR7WpQBNyYb99gaWtXAk4oB5kruChTMOrC2OMuxQRGX6td+0xiUr881YHhJ+" +
                        "LXf96arYR5IdQ0d08OinMfu9FQCbFT5xNeqRBi0plaqPbzjF59Er1e0k6vIrxhgXqn8OXiMiyKCW" +
                        "0ELXsxTL6aEKm5+kLeHm9aSLugOnOVsEyQpeN+Yfx/j17n54TPexQ04sfeP12op3XPK9z0HR7lz5" +
                        "bcJPWpCOs7bIzx4SpLHrzxZgGXjd5zUOsqZ6ozhAJtRrmeKAoyPYwu97HvluXWn1HNVcEpR71u08" +
                        "7RqDae1WxWvzXmDQ6BsRWt4ClWB8a3LREaXxQzClZ6ZwcRWomRm2TnZvvC43C2C9G4NPBhiPUssJ" +
                        "o4rwlbXOzXSjzF6auPpmJcx22J5fAnjpHfyKTxBTymADyeonYB8iKgePJyxOt2PGlTEDOD6q774f" +
                        "D0S4gl38h3YDyhRH2llasuQBTgn5ZcdzlQZo4TkVKLx2tHIwm8tUcDRW7+CqKQkjOYNDZZzIY1cG" +
                        "EpWmkh6+g6RIKSIMbeQC2+y7kj2QeDwzajsWMcFSSyRpf1E05FnSe9fcE8NjjQvhmFoXLYN4fUMH" +
                        "CARthaaBOOldmVQlS5gnvwDYcvaM+Zq87ybOhMkka5p5mYkl2Lr57XcyEFoaFaqYl4i2X+5IpbH4" +
                        "VI6WzEC6A72LFzOyqZfkeDiqmi8JLGuLoHgyyMt0pKPuCYpYQbC2M0gQBC8VU6gkSZSerb2NBr+W" +
                        "oGgkR0DDuB/i1evMlXE4CmzNvPfP1ho357ca9BI0UzqzsfU68fq/m9n9zL+oOIrlQz7jPWt8lZfw" +
                        "6GiMtWks5NEf2LDrvPputs36GXaHq6qa8TOtKpGlL7FNk45tCtVL6d00nxYToPyLJnKSGJGh3BqR" +
                        "ld5c4/tUWvD9SVC2jpwvKE73c17YUSGIeMG43yTDGrgAnHBLGTMxVux+CScj0QU9JzBKyqepcF8L" +
                        "ot7pXy0zBNvuMe14yxqro75/lP1h27x3qGi5Aet9kwntnkc1EuZxQaK5WmFI4p6Q24AXLlv1y041" +
                        "WNq/XJnhvj0q90oeS6s1Hh36OGP6I87rhFd8FjNGfaPBmx/3fEC4HllE4BZ0zk5cTTjWUHWVSAKU" +
                        "lKKIYC6h+LFRqIZG65eoYR9q0jIZplljslYEDFIjSQeDgQVReqI7xqt+DB9LH+aKt037xrmnOwAs" +
                        "m4P4Gl2xNbU7XusCMa2s/5OsxpWmajay3Xiad/xkANdfjhk3XfFKeS7ifBF6dR03hjhUjf9agPkU" +
                        "baWvOFG/EsXCTpexpltuBLq08+PsrV/0vIljE+z/OJHgVs47BmxTsoyoCg9rfUE3g3BtGFzbH0jn" +
                        "jsl+GRiqgmBlb719bPToxw4rphdteEU/pTlwJUg4OtmNgBJpj2/aBbcKYLAmeDclLJeTyT/+v4J7" +
                        "hnOvhtCIDxgKz0EP38ONQTiSsxqzAj2u85TPrmzh6ipyY0OCZVl8gr/u/Yqqwi2Dn4S8zZQ+MMSB" +
                        "xrHruDFQyXK7dPQkwFvkTDzITL82jPjeeWTXhStFrfnhShUh8DwG+gfCFMKIs/rScXoeQF23/tzE" +
                        "xN9zdi6ylQEXPwTPuPgmgXWw3IreRywy3tNqVk3kprAflzOZPw2NPkx9wBSAMEBqFLq7WevQNy4N" +
                        "gmQqfxIhYHUUHKmg7EARbjvW8Oq4Jtnqqsqm/ID6Wv8z4zcC9c8+ttBYWT6arGSw42aaYLCgyQRq" +
                        "ydBH7MBdcshI8k1FVxKUbMEL9kgoCYrdQzbDjxNEaErLYEwtezfPqcqsbTFNm7fApoo51+Lq6Di2" +
                        "ABrx5ShhFUu/XQ6+afD9s30eIrupFUBu9rW4YVmxO9LPnsLZYkpAKTLB9EuH/erOhNEPKc2aFkGr" +
                        "9NOMfQXbuHvOZ1IYFjf8b+cZg/4B3ayg9b8bqOLsrV6jrtYTXlUWJM/b7gBBcjsp2lY2A7feeDAV" +
                        "3dR4yRpHulwZNgJzmKBnAMO8uhuJsY9TwbZKDq0Mc+6slmXJCYqUJekVo81UEozHVbsioZnvhnig" +
                        "ewuocNYTTUe4XPi7dzuSinpn2R69/ozSWX5eMucvvR5fT9KAoIk6k+IwfXwA7AXwgQEycmgQC4M9" +
                        "u7qMNsk4Vb2dhB/9TrMn86AhRiCcJ6k/BxdUhWYqUEe5GW1E0XNhyWaV1S3ZIO+c2KN05L0EzaCq" +
                        "gRUyl1xv5rlxmWPAReti8ZegQ50r53Wsad5Sz674hsfDm1XHwEIuxyx5yqkk+nJ5UysEXqt8y31s" +
                        "EuDST0MQkC50u2MUAjgesraUBgDcDoFte3Flcnqg3T6iFPejFCGWemluDpY0spCGkW/jAzL8OPv+" +
                        "882hyF4KtzJsarJyVY9x1Lgw6ewvE7Iv/mlf6zl/lM03mEqnn29m84kgtY+CTH1+ydOh8Vjnc6Pq" +
                        "mQJKKjzLuCe4YHZQ53dYTGFCdYRjSUPHNKUMR/tNTfBnVVmjnhUWveCJDS2hixtO9cPW8vBiAcIN" +
                        "UcD6JdQPXhafGV2UhNAdq5ywEtBrICxaEZ1+XMeEEknIwL6u2DwFL0vlAHdbkilqX6uldHTCuY2W" +
                        "9CdxlON8t/pkyS8qIj8l3eOKl9s2ClUYB3s94psGSio9RTPo7zbc+cNkbyQ64zfRrlID9hD7/86A" +
                        "Ixu9PZkd/Hkzcxgq+E4hRvscsixn6lcQ+PAhjL+vMMag7Um9zSdYn96tS2ap496HNKlQjoROMpbW" +
                        "8XhlV8Y2HU3Yj+x5ynwAkcdOVfeThTxk+mPjfOmOaV8SEq/KgSq/MtXWFdUzPuR/KcVkI8Ba1wfX" +
                        "wcNob+hfckskqDFOhH+Iu26pFunzIBCRZZrFReNIY6RIdDd2TOu+DhkA8hW2RwfI87qcczGp7oe6" +
                        "Wn6tZ8wX1uN/TOo4TtRpfVsdDUp19qBEdHs0LGe0vlXG77mg+rBe3rArXo7oY5njuC64mkqTbZeG" +
                        "MjFYvp8sSMXhqBjMqzV9j57VEmDw8F1DC/NTucEgJhziI74Er/wp1qt/rAXVT3GOE89oJzbCTLYp" +
                        "9GrxKZfQ5So3u92r0jswdW7U427Kj9pYRJb3FXC+eik9UQvZUEloJyN33HcyZWd4KeZIf1UOplFH" +
                        "9cyBbF4nMRowSuqdwX5qRDEOUhK0tfpPVbN+poQc4VzjTi54bWPhKHBUIWOMFHQoecuRbiBCiuYf" +
                        "ot7jAlBoOlu0mv34OXwjJfwzYSSP90MESiSWg637jyO1BkQxQcpB5BGO9r7cZ75NlnIzlJ7DxSAt" +
                        "0XCO+9A4ssPSdjIP2wzb2p3UzjanbhfOsYmKDeOp6WCU4K/bEEtGC954g5HEhFvluAqSBnNvJ1zH" +
                        "sK8D0/2p/URa80210TbVOiFlWFdADPEoiDHwPdpL3gELxAWjhKRO84cFOow/xUnmANmmx0BNVjj2" +
                        "z0TS4uNSFDu53qiUfTo936bBocSbFqBafrWJdWBLTNJnpsLC6cN7pPUbAPoxLd66zHfQBH1xAyiY" +
                        "R0X5HnLwEpCvC9+HlTIo9dFhX0eCWpWlGEkIz7v6sSsbQfBhbzCpumJFbMS2XGu5QMSJn5DN2KwX" +
                        "QxxGz9KtHSuq404yLvXzZenj5RDZxIx3nNktYAY2Idbc28fmzojoV/d13lGPCoImMs3ROoqmmmC8" +
                        "wTsCFsNZoH7ELdq4fTykIu1pCIhlFE2/bt3plxbWG0zKfsGtojiTFYSFxttz8icE4s6yjx36hqmv" +
                        "H4tfmk9OuZUJqq6c7J8AID2z8xSwQU5YhxdHG+Nxd1kRUr525AFriWfr67tWd6ybcXhc0AMX+Q9i" +
                        "QiFLxj37uYcn4gUG4WQ9QrvFrFVJDyFdHbZPRejBMISedIS6tEMtx7owdNA5cILbY+64bcxVList" +
                        "zpVH1W8rAhXHZ5540jywjXdH91Vz6Q9zUzANjoHvJHX2okuoHz0n97EZIUe9hBjqB8Mm1gimDsdi" +
                        "b3A1gmpZ/Pd5G26HvyflDmTJmFVqKsc1LQiHNrdya4o1gRFLFO15WT0F/bX5bhFogmxvRG4peMaB" +
                        "CP/VJYH8jJwVjhPlKKGXBpcff2RL5Xrk5mfjgvF4bdDdMHSpkMt87xF3wjVw3tSrPIDbiP0n5CkD" +
                        "xnX7ef8KO+Bxx208fClNiTG0MZGrqMV1kzAl84cNgzJamIBVW3HMQH2OSfM4YWTiIJLXt9n1UEvV" +
                        "c6TqOUxVsdalrZh23iIOmyZOYxQnM/FAYzYFAwxUhXshIA6xdrSZWAdvEVIbZ8YlHBz1CpPUn3mW" +
                        "0IZDtANxw1gxzLOZzDoLf9ZJTVSPdwNMBvqx1BYahxp0wnppw4Yp1Ub8r7sqJoDt8GwVhKGGnkHp" +
                        "SGeLGip31af7Lx/YUUuV9Hozgpx0bIiQ5ZR6ZDp3ohVKcTgM0e9AiqTUp2zDVZgChDsmVb2mbOUK" +
                        "bKI7htq4GhGihgWOeVDSf11OpD+cmEtMHrFjl4mJFT9EmztODLrfFq/sjctYgAP0uttyaxsyS52c" +
                        "4vn4w04M9b5qsh92+L5HLFtc9E64N21shUA9LIOfSBVW9+Mmg/PFGU6VO+u0JFyhBKZ9YyqrUgT7" +
                        "vnblDslzGTaS/+l0rcS1mGXXACkDiH/reWKfMVb2BHa61gRTqsynFHVsjN5AeoySShm8TMtZ/rgW" +
                        "lOvEuLFIgDX1Gckvyruyw8i88OuD2r8KILFnGIxal9JJegH84cNBOWDK5VFutrlUG/aF+Ia6K2Lp" +
                        "6rgnpfGn6h6lzXPygH+PmVLOmXFs7pytPto4QC1dsAoYYTr3h/T+p3mD2HfGDeJViStGr2XT25+b" +
                        "s9aKduSbvipHjZrCqVLtxIMkqaMfHWDMqRC1/sgtI3DnD1mWhEBroyrfQIcBww6DbmqxdeeP8khA" +
                        "zUVP0vkW0rzCMaMY1NfinGOZWq8jMxmpciUKmzWR7aQ/D46FsXUL2fkYzthWI2fIviSadqImrKuy" +
                        "YEpDEeNFgt+q4ixomSdC9Wo1YNSZEe0lm0GlpN1bU7TKqCAgVRF1zMGJhxLCvtplG5kN3Ota4EtA" +
                        "Ysu3gyfoEQIq7RNjnTF1xVUdR4N3KJWLHLCR8kbAKgYFebXqVwN0pDpUdvlv8kluT/Qm+0TNTsVZ" +
                        "ydncXzWfRcVJLAW4bpVXsb7C8GxHrqjAp3rK4waOjiBieP3mzcbple3V41YQyzZ5BkHtgI0Rv6ni" +
                        "zw0UEd+ErBcYru2p3kiSKaJL9fVLOy/5uSqbXuFKuj/eaEj2pIutuTAg/WvYG6eT0H7zxeZ6RgZ0" +
                        "IM8+KvJyc4CkN5AD+N4r4HS2wlYLdx/eqq27Y+ePh+4MLHMZ5nnDw405J2aW6fXnc25qcVVBzJ+Y" +
                        "a8EsbahuVXErphvjBqqC1kB7GC4h1Qxr2lwG7VcATYtvCP5XQ8UWGggYjgJHFtwQLlLq1VznPGEd" +
                        "E0N2N9xg8qdSPYKxntg5KuNOJieoxdSo9mvMkdzZx6aVx0878teNYz3Zxy7jvz/JiSw5spjLXMxe" +
                        "janY1azs6K244jPj1tF++9js/+ihlmlNP5Wesdmdjz4bGbqCNgDiCRGmnbEJglgqB5Qzf04fFruP" +
                        "/CxuIxqMgbYds+vt1PDx0TQdwr/B+vrmlPP8JX4SvWNdXq1Kitm4ZSPqPWnG6v65UFmxTlrV50SV" +
                        "37cfATtk7BgJwnwDsUQNiVLL3LVfmylfGZaKqWdawP3ppmyv1mqACxG1GdQsmfRG4qsJVBPUVbIQ" +
                        "EmCsbvT7fSo0K0tqdl6yjXDSFW3ZqAdSWqNgQ5w+eriytTOWXcnKkDNEdRNNvaE7AFw5ID5GNpoK" +
                        "oypn8InBpkofbNmRPVazw4e7vIcwfqIoN2wEhG4WDY/UcPdPuK4dsyrXV4z9B1nL7UCkhHYIWzvG" +
                        "7YTMPbGHxdWPNDoflwyDIikp1PekuNx8aF6nuVx4C+Kknud1aOxaQLYh5Qmjjnvqej6LbP/sY9fb" +
                        "oIqCZ1ds3Qr91Dis6W1+oUUdT6Y2W//bx1YUOt1V2KMQ/bMQZI7fGsSJVIJsGQCJ9JNJ3GOMeaKw" +
                        "7sS2u8zWcgaK4aVtOomAFsB5DBIfYSJzX7K3EjeSu3JbQhVMVvotSLY2kwKofXpmSJTRvCKruAY7" +
                        "fxb5JCnHhSMl+I2tGCVXsd59gg3OGKz+9rFn/4hjHODKZJG8asxkcLqDwA4po+2bs6XfvnHduX5o" +
                        "4OS7DxX7YJUL3klPpZq9nXqKf5PNsBhn5DRxTV02Ih9YKUfpNXChoudmcayk9vxXQJhIk32Ct0Jy" +
                        "KQe7Fbj4e/zy2uIVYMY5f2luyXLiIIn7u1/aBCsqbVgkJA6GTmRhvkEBSgsUzMsmwqSgwsVUK/Wu" +
                        "Fyb5nEUQMfEiU5WqKMiNnG9SrM4XCKU8h3OdMsR95A6RZJyuoOEi0LMIfY26O51zSp5K5DrkhAFf" +
                        "55Oo7Tbel0q65Dv5PkDLPifBd6pnZckl3iq+4d+KXzwYRcWd+HOm42ppPrkKJ41i5BpYuByBbYbK" +
                        "ptrkYfyYD38nv0+LamL2N+HB90omA0qTUbSzcAVGF00zIbHfCV8eQix4wIhiZhPxYB0VfB3OPv6l" +
                        "itcDsy4mOoaNeiTHs0rE1hXyjZkKG0LxVN/nBOheEVNwoqOzW3LGYkcQhi5hq9BpMEzbfGX4q7YZ" +
                        "3242LcBsX2zLlrrLxNXAL0oczZZsqUkfxSbWFMG2JG+TP4UeWGQFCjMzAdZ2C9P0US8vWVkWUDgQ" +
                        "qeq6j31//Wg/3q67XGtrTLw0C6VNbWJ9rv5MhExXvhUw1fnisjYF2mLdkSzTKVD4xoIh0ErP7x6/" +
                        "7UO1imbFSzUqdu87f6Idbr81xMd5DTdfhe2YlOdEE1YoaxTzbXE0fKD0Z0xTuACWMqtdfIlHrnki" +
                        "J15V1v/2sTvuW79Fz4pr4wGrnWjR6CxrpJDGUgqdG7naPo4+TCm+JU5QkWSgcluAEJXyuckA8caK" +
                        "ew5H3jLa9T6aO84IrafDYEEyTf8LQNAQYxfF1s1RzPwxQuwXlIwdVpm1PNX6QhrNarxP3fFzAkxe" +
                        "ayS5xiKjlGtxFSeqLU207nyNE/nXEl+jVM8auvXXVCDL63heAenC82MEJd0jSlZTEQPyDZ1KTuc+" +
                        "9oYbsY/tvwMHLSNw7QwsD667n4b7I8vMnMI72PxNpWqpfLOP3XEVjcJQrBW73tO743LXQQ2HEw8g" +
                        "tExApodXKFNefS/OoPl0GrPIwmR4JAWKYOZaO/dh71yI3pgioJRMOv3ztF1FU+4Ti00CknQwaqJe" +
                        "4SDlWBohpys2cPb7Ruk+rYJZJpEb97HLsQHT55j+kjJvfgJp2IdrxlgTvI1zEgVqdF3z0LC8fBz+" +
                        "1jXxVLwcPzMsyr/t5shiJsz8OU0hxZME9Y4jiuebToz3OuYGFabEukGuYLOPHTIBb6vKZodzrQKp" +
                        "xj8YR/rbyjvO3UyLYD+6rNc/4EPe77UUoqUEA1PSYRIEuM97m/ELm97ifdD2nSmXaN2Ji9Ha8MtE" +
                        "iCagVWI2jCg0m5SKE7DmTDbWs+F/9mONbx8bZhHkfm4LmqLd+MiOIU88fofTLCT2OkYqpwNcCS4K" +
                        "wNF5insltZ236gTgIG3EvtzK53Ry1k+Ir2ugyCk5U4Jw+pH73VjvDxTHh+0xSlkUmeYY1s9aiwS5" +
                        "AnJUbWyJ7U/5fU/JrApnp5/5Uxq9M7FNeY3nDCCYn4XqGNlmy/jc8c1UhqOsaZ2n1vLAVR+J8YYI" +
                        "+Cdw3DFJHq7ckSz8sZlrzqEyDoeVEEBvpdEL9YDQUnod00QHdILBEKXFVI5VApetNjnRRnpDI0/r" +
                        "lIWombWoC3KHOKkjoELBbeHV6y93fUQ4XdpSE5ezTYyLhxEsZKty2HpEa2HU6a1gCE1O/w4ui1GD" +
                        "W9hmVjrETEnwNQCmMdduAJmW3SSVWuoM8dBiMcpVJghKtYrV1ErHAFF22N1Z7Gu6XbaktbKQzuAM" +
                        "CK+AbuUYuMtURTL66aae56EZg1lg4MDPpX21KYyaj/nunyAvC3hQiftcKFovSauE+aEd35V1YQzx" +
                        "d9iMhHoH6fYLXptVyzsPs2Hnj4OypW0wYG28v8rd3L0c6xmcBwl2b5YtZKYtnJT5xIYA9eMDIOa4" +
                        "OfVnHzsOzBpmIRGwREsDZjVgypkxPCOWgas8kX5vVimW9mXAYjJocqWW99ULYBrpibrLDvEoG0Pb" +
                        "VltE2w2mjuvdFUn2LQsVXk5XFkkfuwKs5EQxLFSWqqyRm2p5L96VJg3RYDJ72c6M3wqE6U2mSZLG" +
                        "ElPC8kdNNbrBnma7EYm2JKmY4PpIVUUrGuYMvjiUk36pzS+ssvrtY7NvYoAJcAyaGInAZpBxPETS" +
                        "+NLBTmVgJ7rxIvinw3iz+BibfWwoUrHioMfgfyUYJLk77gkFvaqm19nrPjbEbMsrVpfVn00x6NY+" +
                        "16Wyj/1FC00P4v/QXfh+BAERW3QAdEopjiMy1hwYv1g0ujmlufahP8KajLRkBDVR3rY/HmR9e5t+" +
                        "DLwD9VH2sbVj+ZhsVKmTJ6KGPPlt81CKPCa2SVjW6Ongha2NMP5sXLpTGR119Txpn4eeG08lzs3K" +
                        "S7yVaHgm7z2thKskZD47V8lLhBjIz+8xzIv9C+QXKo8pVzCD8vHYSr0gVyjfOll3xaAGLEaJc6gG" +
                        "vFd2j+rFS3wCUhrgojqeo19Edk9Vpr2mJ8pZEyo8u6dsmLql1S8wmBc7LcAMhEaCHKPbUbV6AxUO" +
                        "61Sox+oAJewM4aYVN8ZOd55tAEVoo2ZzsgHZGt6Tn7e20c4fMYWmh5IA3pCtTlPrlupuXa12vmGK" +
                        "if6HbT9HSmaGcsBdQkOQ70POyuLRvVyhRpnwx8VQZFa5PBLuxWm3iDh/UhBMCq3HdR876ViVHFn7" +
                        "5TXyypjAeKesihjAk7FiZPMrb7MyCpKsAogbYxjmb7IWFsr2U/uOMb0QXuxPiuAVANyVec2fldHJ" +
                        "KCXYaADG3Nr08y5N5kRgYrThG2Cq4uEtYLdQdTdQdLmjDUTRFnBHf5jfVlmZFaFZ4w47WlqJw6dY" +
                        "Nnx7XchSY7leko7SBsVQ7ZAKttQOySP6e2WOv3/9B2z4DPRQwwAA";
        try {
            DNA = new LineNumberReader(
                  new InputStreamReader(
                  new GZIPInputStream(
                  new ByteArrayInputStream(
                  new BASE64Decoder().decodeBuffer(z))))).readLine().toLowerCase();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Test
    public void test007() {
        PatternSet pat = RegexCompiler.compile("007", "008");
        IndexedString s1 = pat.match("as00haklsdjhfla00");
        IndexedString s2 = pat.match("7jhd7dsh007dsfa");
        System.out.println(s1.append(s2).getMatches());
    }

    @Test
    public void testOverlappingMatches() {
        PatternSet pat = RegexCompiler.compile("abra", "braha");
        IndexedString s = pat.match("habrahabr");
        System.out.println(s.getMatches());
    }

    @Test
    public void testDNA() throws IOException {
        String[] regexes = new String[]{
                "[cgt]gggtaaa|tttaccc[acg]",
                "a[act]ggtaaa|tttacc[agt]t",
                "ag[act]gtaaa|tttac[agt]ct",
                "agg[act]taaa|ttta[agt]cct",
                "aggg[acg]aaa|ttt[cgt]ccct",
                "agggt[cgt]aa|tt[acg]accct",
                "agggta[cgt]a|t[acg]taccct",
                "agggtaa[cgt]|[acg]ttaccct",
        };

        PatternSet pat = RegexCompiler.compile(regexes);

        IndexedString idna = pat.match(DNA);

        int[] expectedFreq = new int[regexes.length];
        int[] actualFreq = new int[regexes.length];

        for (Match m : idna.getMatches()) {
            ++actualFreq[m.whichPattern()];
        }

        for (int i = 0; i < regexes.length; i++) {
            String regex = regexes[i];
            Matcher m = Pattern.compile(regex).matcher(DNA);
            for (int s = 0; m.find(s); s = m.start() + 1) {
                ++expectedFreq[i];
            }
        }

        for (int i = 0; i < regexes.length; ++i) {
            assertEquals(expectedFreq[i], actualFreq[i]);
        }
    }

    @Test
    @Ignore("Not really a test")
    public void testPerformance() {
        String[] regexes = new String[]{
                "[cgt]gggtaaa|tttaccc[acg]",
                "a[act]ggtaaa|tttacc[agt]t",
                "ag[act]gtaaa|tttac[agt]ct",
                "agg[act]taaa|ttta[agt]cct",
                "aggg[acg]aaa|ttt[cgt]ccct",
                "agggt[cgt]aa|tt[acg]accct",
                "agggta[cgt]a|t[acg]taccct",
                "agggtaa[cgt]|[acg]ttaccct",
        };

        PatternSet pat = RegexCompiler.compile(regexes);
        Pattern[] pats = new Pattern[regexes.length];
        for(int i = 0; i < regexes.length; ++i) {
            pats[i] = Pattern.compile(regexes[i]);
        }

        for(int n = 1; n < 50; ++n) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < n; ++i) {
                sb.append(DNA);
            }
            String dna = sb.toString();

            int[] expectedFreq = new int[regexes.length];
            int[] actualFreq = new int[regexes.length];

            long t0 = System.nanoTime();
            IndexedString idna = pat.match(dna);
            long ireIndexingTimeMs = (System.nanoTime() - t0)/1000000L;
            t0 = System.nanoTime();
            for(Match m : idna.getMatches()) {
                ++actualFreq[m.whichPattern()];
            }
            long ireMatchingTimeMs = (System.nanoTime() - t0)/1000000L;

            t0 = System.nanoTime();
            for (int i = 0; i < pats.length; i++) {
                Pattern p = pats[i];
                Matcher m = p.matcher(dna);
                for (int s = 0; m.find(s); s = m.start() + 1) {
                    ++expectedFreq[i];
                }
            }
            long javaMatchingTimeMs = (System.nanoTime() - t0)/1000000L;

            System.out.println(
                    n + " " + ireIndexingTimeMs + " " + ireMatchingTimeMs + " " +
                    (ireIndexingTimeMs + ireMatchingTimeMs) + " " + javaMatchingTimeMs);
        }
    }
}
