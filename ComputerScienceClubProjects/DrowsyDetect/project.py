#Ishaan Gupta

import cv2
import os
from keras.models import load_model
import numpy as np
import time
from scipy.spatial import distance as dist
from imutils.video import VideoStream
from imutils import face_utils
from threading import Thread
import imutils
import dlib
import math
import time

#calculates EAR
def eye_aspect_ratio(eye):
	A = dist.euclidean(eye[1], eye[5])
	B = dist.euclidean(eye[2], eye[4])
	C = dist.euclidean(eye[0], eye[3])
	ear = (A + B) / (2.0 * C)
	return ear

#calculates MAR
def mouth_aspect_ratio(mouth):
	D = dist.euclidean(mouth[14], mouth[18])
	E = dist.euclidean(mouth[12], mouth[16])
	mar = (1.0 * D)/E
	return mar

#calculates PUC
def pupil_circularity(pupil):
	area = math.pow(dist.euclidean(pupil[1], pupil[4]), 2)/2.0 * math.pi
	perimeter = dist.euclidean(pupil[0], pupil[1]) + dist.euclidean(pupil[1], pupil[2]) + dist.euclidean(pupil[2], pupil[3]) + dist.euclidean(pupil[3], pupil[4]) + dist.euclidean(pupil[4], pupil[5]) + dist.euclidean(pupil[5], pupil[0])
	puc = (4.0 * area * math.pi)/(math.pow(perimeter, 2))
	return puc

#Haar Cascades files
leye = cv2.CascadeClassifier('haarcascade_lefteye_2splits.xml')
reye = cv2.CascadeClassifier('haarcascade_righteye_2splits.xml')

#pre-trained model
model = load_model('cnncat2.h5')


rpred=[99]
lpred=[99]

#Starts the video stream
vs = cv2.VideoCapture(0)

#initializes the facial landmark detector
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor("shape_predictor_68_face_landmarks.dat")

#gets the coordinates from the facial landmark predictor from the eyes and mouth
(lStart, lEnd) = face_utils.FACIAL_LANDMARKS_IDXS["left_eye"]
(rStart, rEnd) = face_utils.FACIAL_LANDMARKS_IDXS["right_eye"]
(mStart, mEnd) = face_utils.FACIAL_LANDMARKS_IDXS["mouth"]

#this is how many frames the user has to be sleepy for to be declared sleepy
CONSEC_FRAMES = 10

#these calues measure how many consecetive frames the user is sleepy for in each category
MOUTH_COUNTER = 0
EYE_COUNTER = 0
PUC_COUNTER = 0
RIGHT_COUNTER = 0
LEFT_COUNTER = 0

#this is how many frames are used for calibration
CALIB_TIME = 150

#the total sum of these values which is used to find the average during calibration
eye_sum = 0
puc_sum = 0
mar_sum = 0

#Runs this loop for CALIB_TIME frames
for i in range(0, CALIB_TIME):
    #reads in a frame from the video stream
    ret, frame = vs.read()

    #converts the image to grayscale becasue the deep learning facial landmark detector uses grayscale images
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    #locates the faces
    rects = detector(gray, 0)

    #loops through all located faces
    for rect in rects:

        shape = predictor(gray, rect)
        shape = face_utils.shape_to_np(shape)

        leftEye = shape[lStart:lEnd]

        rightEye = shape[rStart:rEnd]

        mouth = shape[mStart:mEnd]


        leftEAR = eye_aspect_ratio(leftEye)
        rightEAR = eye_aspect_ratio(rightEye)
        leftPUC = pupil_circularity(leftEye)
        rightPUC = pupil_circularity(rightEye)

        mar_sum += mouth_aspect_ratio(mouth)
        eye_sum += (leftEAR + rightEAR) / 2.0
        puc_sum += (leftPUC + rightPUC) / 2.0


        cv2.putText(frame, "CALIBRATING...", (110, 50),
                    cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 4)
    cv2.imshow("Frame", frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

N_EYE = eye_sum/CALIB_TIME
N_PUC = puc_sum/CALIB_TIME
N_MAR = mar_sum/CALIB_TIME


begin = time.time()
sleep_frames = 0

while(True):
    ret, frame = vs.read()

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    left_eye = leye.detectMultiScale(gray)
    right_eye =  reye.detectMultiScale(gray)

    sleepy = False

    for (x,y,w,h) in right_eye:
        #frame = cv2.rectangle(frame, (x,y), (x+w , y+h), (0,255,0), 3)
        r_eye = frame[y:y+h,x:x+w]
        r_eye = cv2.cvtColor(r_eye,cv2.COLOR_BGR2GRAY)
        r_eye = cv2.resize(r_eye,(24,24))
        r_eye= r_eye/255
        r_eye=  r_eye.reshape(24,24,-1)
        r_eye = np.expand_dims(r_eye,axis=0)
        rpred = model.predict_classes(r_eye)
        if (rpred[0] == 0):
            #frame = cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 0, 255), 3)
            RIGHT_COUNTER += 1
            if RIGHT_COUNTER >= CONSEC_FRAMES:
                sleepy = True
        else:
            #frame = cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 3)
            RIGHT_COUNTER = 0
        break

    for (x,y,w,h) in left_eye:
        #frame = cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 3)
        l_eye=frame[y:y+h,x:x+w]
        l_eye = cv2.cvtColor(l_eye,cv2.COLOR_BGR2GRAY)
        l_eye = cv2.resize(l_eye,(24,24))
        l_eye= l_eye/255
        l_eye=l_eye.reshape(24,24,-1)
        l_eye = np.expand_dims(l_eye,axis=0)
        lpred = model.predict_classes(l_eye)

        if(lpred[0]==0):
            #frame = cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 0, 255), 3)
            LEFT_COUNTER += 1
            if LEFT_COUNTER >= CONSEC_FRAMES:
                sleepy = True
        else:
            #frame = cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 3)
            LEFT_COUNTER = 0
        break

    rects = detector(gray, 0)

    ear = 0.0
    mar = 0.0
    puc = 0.0



    for rect in rects:
        shape = predictor(gray, rect)
        shape = face_utils.shape_to_np(shape)

        leftEye = shape[lStart:lEnd]

        #frame = cv2.line(frame, (leftEye[0][0], leftEye[0][1]), (leftEye[3][0], leftEye[3][1]) , (0, 255, 0), 3)
        #frame = cv2.line(frame, (leftEye[1][0], leftEye[1][1]), (leftEye[5][0], leftEye[5][1]), (0, 255, 0), 3)
        #frame = cv2.line(frame, (leftEye[2][0], leftEye[2][1]), (leftEye[4][0], leftEye[4][1]), (0, 255, 0), 3)

        rightEye = shape[rStart:rEnd]

        #frame = cv2.line(frame, (rightEye[0][0], rightEye[0][1]), (rightEye[3][0], rightEye[3][1]), (0, 255, 0), 3)
        #frame = cv2.line(frame, (rightEye[1][0], rightEye[1][1]), (rightEye[5][0], rightEye[5][1]), (0, 255, 0), 3)
        #frame = cv2.line(frame, (rightEye[2][0], rightEye[2][1]), (rightEye[4][0], rightEye[4][1]), (0, 255, 0), 3)

        mouth = shape[mStart:mEnd]
        #jaw = shape[jStart:jEnd]

        #frame = cv2.line(frame, (mouth[12][0], mouth[12][1]), (mouth[16][0], mouth[16][1]), (0, 255, 0), 3)
        #frame = cv2.line(frame, (mouth[14][0], mouth[14][1]), (mouth[18][0], mouth[18][1]), (0, 255, 0), 3)

        leftEAR = eye_aspect_ratio(leftEye)
        rightEAR = eye_aspect_ratio(rightEye)
        mar = mouth_aspect_ratio(mouth)
        leftPUC = pupil_circularity(leftEye)
        rightPUC = pupil_circularity(rightEye)

        ear = (leftEAR + rightEAR) / 2.0
        puc = (leftPUC + rightPUC) / 2.0

        if ear < N_EYE - 0.1:
            EYE_COUNTER += 1
            if EYE_COUNTER >= CONSEC_FRAMES:
                sleepy = True
        else:
            EYE_COUNTER = 0

        if mar > N_MAR + 0.1:
            MOUTH_COUNTER += 1
            if MOUTH_COUNTER >= CONSEC_FRAMES:
                sleepy = True
        else:
            MOUTH_COUNTER = 0

        if puc < N_PUC - 0.1:
            PUC_COUNTER += 1
            if PUC_COUNTER >= CONSEC_FRAMES:
                sleepy = True
        else:
            PUC_COUNTER = 0

    if sleepy:
        sleep_frames += 1
        cv2.putText(frame, "DROWSY", (10, 200), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
    else:
        cv2.putText(frame, "ALERT", (10, 200), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)

    cv2.putText(frame, "EAR: {:.2f}".format(ear), (300, 50),
                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
    cv2.putText(frame, "MAR: {:.2f}".format(mar), (10, 30),
                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
    cv2.putText(frame, "PUC: {:.2f}".format(puc), (300, 30),
                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
    cv2.putText(frame, "N_EYE: {:.2f}".format(N_EYE), (225, 100),
                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
    cv2.putText(frame, "N_MAR: {:.2f}".format(N_MAR), (10, 100),
                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
    cv2.putText(frame, "N_PUC: {:.2f}".format(N_PUC), (400, 100),
                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)

    time_elapsed = time.time() - begin
    portion = sleep_frames/time_elapsed

    cv2.putText(frame, "Ratio: {:.2f}".format(portion), (400, 400),
                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)

    if portion < 1:
        cv2.putText(frame, "GREEN", (400, 300),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
    elif portion < 3:
        cv2.putText(frame, "YELLOW", (400, 300),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 255), 2)
    else:
        cv2.putText(frame, "RED", (400, 300),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)





    cv2.imshow('frame',frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

vs.release()
cv2.destroyAllWindows()
