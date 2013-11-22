#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include <opencv2/highgui/highgui.hpp>
#include <iostream>
#include <stdio.h>
#include <math.h>
#include <android/log.h>


using namespace std;
using namespace cv;


void vector2Mat(vector<KeyPoint> keypoints, Mat* MatkeyPoints){
	int k_size = (int)(keypoints.size());
	for(int i=0;i<k_size;i++){
		Mat t =(Mat_<float>(1,2) << keypoints[i].pt.x, keypoints[i].pt.y);
		(*MatkeyPoints).push_back(t);
	}
//	vector<cv::Point2f> points;
//	vector<KeyPoint>::iterator it;
//	for( it= keypoints.begin(); it!= keypoints.end();it++)
//	{
//	    points.push_back(it->pt);
//	}
//	Mat pointmatrix(points);
//	*MatkeyPoints = pointmatrix;
}

//JNIEXPORT void JNICALL Java_org_opencv_samples_tutorial4_Sample4View_FindFeatures(JNIEnv* env, jobject thiz, jlong addrGray, jlong addrRgba)
extern "C" {
JNIEXPORT void JNICALL Java_tw_edu_ntu_netdb_demo_MOSROHelper_FindFeatures(JNIEnv* env, jobject thiz, jlong addrGray, jlong addrRgba, jlong keyPointsObj, jlong descriptorObj)
{
    Mat* pMatGr=(Mat*)addrGray;
    Mat* pMatRgb=(Mat*)addrRgba;
    Mat* pMatkeyPoints=(Mat*)keyPointsObj;
    Mat* pMatdescriptor=(Mat*)descriptorObj;

    vector<KeyPoint> keypoints;
    keypoints.clear();
    char LogMsg[128];

    // parameters for SIFT
    double threshold = 0.12f;
    double edgeThreshold = 10.0f;
    int nOctaves=SIFT::CommonParams::DEFAULT_NOCTAVES;
    int nOctaveLayers=SIFT::CommonParams::DEFAULT_NOCTAVE_LAYERS;
	int firstOctave=SIFT::CommonParams::DEFAULT_FIRST_OCTAVE;
	int angleMode=SIFT::CommonParams::FIRST_ANGLE;
	double magnification = 3.0f;
	bool isNormalize = true;
	bool recalculateAngles = true;

	// constructors
	cv::SIFT::DetectorParams siftDetectorParams(threshold, edgeThreshold);
    cv::SIFT::CommonParams siftCommonParams(nOctaves,nOctaveLayers,firstOctave,angleMode);
    cv::SIFT::DescriptorParams siftDescriptorParams(magnification, isNormalize, recalculateAngles);
    cv::SiftFeatureDetector* SiftDetector = new cv::SiftFeatureDetector(siftDetectorParams, siftCommonParams);
    cv::SiftDescriptorExtractor* SiftDescriptor = new cv::SiftDescriptorExtractor(siftDescriptorParams, siftCommonParams);
	sprintf(LogMsg, "Start with params: (%.2f,%.2f) (%d,%d,%d,%d)\n", threshold,edgeThreshold,nOctaves,nOctaveLayers,firstOctave,angleMode);
    __android_log_write(ANDROID_LOG_DEBUG, "FindFeatures", LogMsg);

    // SIFT detect
    SiftDetector->detect(*pMatGr, keypoints);
    sprintf(LogMsg, "keypoints: %d\n", (int)(keypoints.size()));
    __android_log_write(ANDROID_LOG_DEBUG, "FindFeatures", LogMsg);

    // To Opponent color space
    Mat R, G, B, O1, O2, O3, d1, d2, d3;
    vector<KeyPoint> k1=keypoints, k2=keypoints, k3=keypoints;
    vector<Mat> channels(3);
    split(*pMatRgb, channels);
    // get the channels (dont forget they follow BGR order in OpenCV)
    R = channels[0];
    G = channels[1];
    B = channels[2];
    O1 = (R - G) / sqrt(2);
    O2 = (R + G - B*2) / sqrt(6);
    O3 = (R + G + B) / sqrt(3);
    cv::SiftDescriptorExtractor* descriptorExtractor1 = new cv::SiftDescriptorExtractor(siftDescriptorParams, siftCommonParams);
    cv::SiftDescriptorExtractor* descriptorExtractor2 = new cv::SiftDescriptorExtractor(siftDescriptorParams, siftCommonParams);
    cv::SiftDescriptorExtractor* descriptorExtractor3 = new cv::SiftDescriptorExtractor(siftDescriptorParams, siftCommonParams);
    descriptorExtractor1->compute(O1, k1, d1);
    sprintf(LogMsg, "descriptors: %d (%d, %d)\n", (int)(k1.size()),d1.rows, d1.cols);
	__android_log_write(ANDROID_LOG_DEBUG, "FindFeatures", LogMsg);
    descriptorExtractor2->compute(O2, k2, d2);
    sprintf(LogMsg, "descriptors: %d (%d, %d)\n", (int)(k2.size()),d2.rows, d2.cols);
    __android_log_write(ANDROID_LOG_DEBUG, "FindFeatures", LogMsg);
    descriptorExtractor3->compute(O3, k3, d3);
    sprintf(LogMsg, "descriptors: %d (%d, %d)\n", (int)(k3.size()),d3.rows, d3.cols);
    __android_log_write(ANDROID_LOG_DEBUG, "FindFeatures", LogMsg);

    // Descriptors Combine
    int k1_size = (int)(k1.size());
	int k2_size = (int)(k2.size());
	int k3_size = (int)(k3.size());
    vector<KeyPoint> k;
    Mat d;
    int k1_ind=0;
    int k2_ind=0;
    int k3_ind=0;
    float ep = 0.05;
    while(k1_ind<k1_size){
    	float x = k1[k1_ind].pt.x;
    	float y = k1[k1_ind].pt.y;
    	// find k2
    	bool isFoundk2 = false;
    	for(int i=k2_ind; i<k2_size && isFoundk2==false; i++){
    		if (abs(k2[i].pt.x-x)<=ep && abs(k2[i].pt.y-y)<=ep){
    			isFoundk2 = true;
    			k2_ind = i;
    		}
    	}
    	bool isFoundk3 = false;
    	// find k3
    	for(int i=k3_ind; i<k3_size && isFoundk2==true && isFoundk3==false; i++){
    		if (abs(k3[i].pt.x-x)<=ep && abs(k3[i].pt.y-y)<=ep){
    			isFoundk3 = true;
    			k3_ind = i;
    		}
    	}
    	if (isFoundk2==true && isFoundk3==true){
    		k.push_back(k1[k1_ind]);
    		Mat t = d1.row(k1_ind).clone();
    		hconcat(t, d2.row(k2_ind), t);
    		hconcat(t, d3.row(k3_ind), t);
    		d.push_back(t);
    	}
    	k1_ind++;
    	// go to next keypoints (eliminate duplicate)
    	for (int i=k1_ind; i<k1_size; i++){
    		if (abs(k1[i].pt.x-x)>=ep || abs(k1[i].pt.y-y)>=ep){
    			k1_ind = i;
    			break;
    		}
    	}
    }
    sprintf(LogMsg, "cDes: %d (%d, %d)\n", (int)(k.size()),d.rows,d.cols);
    __android_log_write(ANDROID_LOG_DEBUG, "FindFeatures", LogMsg);

    vector2Mat(k, pMatkeyPoints);
    __android_log_print(ANDROID_LOG_DEBUG, "FindFeatures", "keypoint2Mat");
    *pMatdescriptor = d;

//    for( size_t i = 0; i < k.size(); i++ )
//    	circle(*pMatRgb, Point(k[i].pt.x, k[i].pt.y), 10, Scalar(255,0,0,255));

}

}
