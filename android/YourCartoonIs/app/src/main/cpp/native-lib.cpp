// native-lib.cpp
//
// Created by min on 2020-07-26.
//

#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/features2d.hpp>
#include <opencv2/videoio.hpp>
#include <android/log.h>


//C++
#include <iostream>
#include <sstream>
#include <vector>

using namespace cv;
using namespace std;

#define LOG_TAG "C++"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, __VA_ARGS__)

extern "C" {
JNIEXPORT void JNICALL
Java_com_e_yourcartoonis_MainActivity_ConvertRGBtoGray(
        JNIEnv *env,
        jobject  instance,
        jlong matAddrInput,
        jlong matAddrResult) {

    LOGE(LOG_TAG, "Start convertRGB().");
    Mat &matInput = *(Mat *) matAddrInput;
    LOGE(LOG_TAG, "matInput access success.");
    Mat &matResult = *(Mat *) matAddrResult;
    LOGE(LOG_TAG, "matOutput access success.");

    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
}
JNIEXPORT jstring JNICALL
Java_com_e_yourcartoonis_MainActivity_setText(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("Hello Jni!");
}
double CombineSimilarity(Mat img1,Mat img2){

}
class centroid_info{
public:
    int num;
    int start;
    int numOfFrame;
    float feature[4];
    void glcm(Mat &img){
        float energy=0,contrast=0,homogenity=0,entropy=0;
        int row=img.rows,col=img.cols;
        Mat gl=Mat::zeros(416,416,CV_32FC1);

        //creating glcm matrix with 256 levels,radius=1 and in the horizontal direction
        for(int i=0;i<row;i++)
            for(int j=0;j<col-1;j++)
                gl.at<float>(img.at<uchar>(i,j),img.at<uchar>(i,j+1))=gl.at<float>(img.at<uchar>(i,j),img.at<uchar>(i,j+1))+1;

        // normalizing glcm matrix for parameter determination
        gl=gl+gl.t();
        gl=gl/sum(gl)[0];


        for(int i=0;i<416;i++) {
            for (int j = 0; j < 416; j++) {
                energy = energy +
                         gl.at<float>(i, j) * gl.at<float>(i, j);            //finding parameters
                contrast = contrast + (i - j) * (i - j) * gl.at<float>(i, j);
                homogenity = homogenity + gl.at<float>(i, j) / (1 + abs(i - j));
                if (gl.at<float>(i, j) != 0)
                    entropy = entropy - gl.at<float>(i, j) * log10(gl.at<float>(i, j));
            }
        }
        this->feature[0] = energy;
        this->feature[1] = contrast;
        this->feature[2] = homogenity;
        this->feature[3] = entropy;
    }
};

JNIEXPORT jobjectArray JNICALL
Java_com_e_yourcartoonis_VideoTransfer_extractKeyFrame(JNIEnv *env, jobject instance,
        jlongArray matAddrList,jint *clusterSize) {
    Mat hist1,hist2;
    jlong *addrList;
    int i,j;
    double similarity;
    double result;
    vector<centroid_info> centroid;
    int size = env->GetArrayLength(matAddrList);
    LOGE(LOG_TAG,"SIZE : %d",size);
    addrList = env->GetLongArrayElements(matAddrList,NULL);
    float range[] = {0.0,256.0};
    const float* histRange = {range};
    int number_bins = 256;
    Mat *img = new Mat[size];
    Mat *hist = new Mat[size];
    LOGE(LOG_TAG,"IN Histogram Intersect");

    Mat &input = *(Mat*) addrList[0];
    centroid_info tmp;
    tmp.start = 0;
    tmp.num = 0;
    tmp.numOfFrame = 1;
    resize(input,input,Size(416,416),0,0,INTER_CUBIC);
    tmp.glcm(input);
    centroid.push_back(tmp);

    cvtColor(input,img[0], COLOR_RGB2GRAY);
    calcHist(&img[0],1,0,Mat(),hist[0],1,&number_bins,&histRange);
    for(i=1;i<size;i++){
        centroid_info tmp;
        tmp.num = i;
        tmp.numOfFrame = 1;
        Mat &input = *(Mat*) addrList[i];
        resize(input,input,Size(416,416),0,0,INTER_CUBIC);
        tmp.glcm(input);
        cvtColor(input,img[i], COLOR_RGBA2GRAY);
        calcHist(&img[i],1,0,Mat(),hist[i],1,&number_bins,&histRange);
        normalize(hist[i], hist[i]);
    //hist[i] = hist[i].reshape(0,1);
        similarity = 0.7*compareHist(hist[i],hist[centroid.back().num],HISTCMP_INTERSECT);
        float sum = 0;
        for(j=0;j<4;j++){
            sum += (tmp.feature[i] - centroid.back().feature[i])*(tmp.feature[i] - centroid.back().feature[i]);
        }
        sum = sqrt(sum);
        LOGE(LOG_TAG,"image %d feature sum : %lf",i,sum);
        similarity -= 0.3*sum;
        LOGE(LOG_TAG,"image %d similarity : %lf",i,similarity);
        if(similarity <= 0.7) {
            tmp.start = i;
            centroid.push_back(tmp);
        }
        else {
            centroid.back().numOfFrame++;
            centroid.back().num = centroid.back().start + centroid.back().numOfFrame/2;
            Mat &input = *(Mat*) addrList[centroid.back().num];
            centroid.back().glcm(input);
        }
    }
    LOGE(LOG_TAG,"end calc similarity");
    int len = centroid.size();

    Mat &input2 = *(Mat*) addrList[20];
    Mat output;
    Mat output2;
    delete img;
    delete hist;
    env->ReleaseLongArrayElements(matAddrList,addrList,0);
    jintArray init = env->NewIntArray(len);
    jclass cls = env->FindClass("[I");
    jobjectArray re = env->NewObjectArray(len,cls,init);
    jint fill[len][2];
    for(i=0;i<len;i++) {
        jintArray inner = env->NewIntArray(2);
        LOGE(LOG_TAG,"%d centroid : %d",i,centroid[i].num);
        fill[i][0] = centroid[i].num;
        fill[i][1] = centroid[i].numOfFrame;
        env->SetIntArrayRegion(inner,0,2,fill[i]);
        env->SetObjectArrayElement(re,i,inner);
        env->DeleteLocalRef(inner);
    }
    return re;
}
void android_main(struct android_app *state) {
    LOGE(LOG_TAG, "Start android_main().");
}
}