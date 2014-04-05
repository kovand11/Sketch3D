/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class hu_kovand_sketch3d_geometry_CurveLib */

#ifndef _Included_hu_kovand_sketch3d_geometry_CurveLib
#define _Included_hu_kovand_sketch3d_geometry_CurveLib
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     hu_kovand_sketch3d_geometry_CurveLib
 * Method:    approximate
 * Signature: ([D[D[DII)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_hu_kovand_sketch3d_geometry_CurveLib_approximate
  (JNIEnv *, jclass, jdoubleArray, jdoubleArray, jdoubleArray, jint, jint);

/*
 * Class:     hu_kovand_sketch3d_geometry_CurveLib
 * Method:    evaluateN
 * Signature: ([D[D[D[DI)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_hu_kovand_sketch3d_geometry_CurveLib_evaluateN
  (JNIEnv *, jclass, jdoubleArray, jdoubleArray, jdoubleArray, jdoubleArray, jint);

/*
 * Class:     hu_kovand_sketch3d_geometry_CurveLib
 * Method:    evaluate
 * Signature: ([D[D[D[DF)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_hu_kovand_sketch3d_geometry_CurveLib_evaluate
  (JNIEnv *, jclass, jdoubleArray, jdoubleArray, jdoubleArray, jdoubleArray, jfloat);

/*
 * Class:     hu_kovand_sketch3d_geometry_CurveLib
 * Method:    projectPoint
 * Signature: ([D[D[D[DDDDIDD)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_hu_kovand_sketch3d_geometry_CurveLib_projectPoint
  (JNIEnv *, jclass, jdoubleArray, jdoubleArray, jdoubleArray, jdoubleArray, jdouble, jdouble, jdouble, jint, jdouble, jdouble);

#ifdef __cplusplus
}
#endif
#endif
