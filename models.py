#!/usr/bin/python
# -*- coding: latin-1 -*-
import os
from math import sqrt
import numpy as np
import pandas as pd
from neuralNetwork import NeuralNetwork
from copy import deepcopy
#from neuralNetwork import soustrat
#from neuralNetwork import vec_seuil
from sklearn.decomposition import PCA


def PCA_applied(train_input,test_input,pourcent):
    trunc=len(train_input)+1
    data=deepcopy(train_input)
    for elt in test_input:
            data= np.vstack([data,elt])
    print("la taille des donnees est: ",len(data))
    model = PCA(n_components=pourcent)
    new_data=model.fit_transform(data)
    return data[:trunc],data[trunc:],model.n_components_

def toFloat(val):
	if(len(val) == 0):
		return 0.0
	else:
		return float(val)

def openFile(file,nbre_inputRow):
	input=[]
	output=[]
	cin = open(file, 'r')
	line = cin.readline() # Skip the first line
	line = cin.readline()
	while(len(line) > 0):
		p = line.replace('\n', '').split(';')
		inputRow = []
		outputRow = []
		for i in range(nbre_inputRow):
			#print(' val = ' + p[i])
			inputRow.append(toFloat(p[i]))
		for i in range(nbre_inputRow, len(p)-1):
			outputRow.append(toFloat(p[i]))
		input.append(inputRow)
		output.append(outputRow)
		# print(' -- line = ' + str(pos))
		line = cin.readline()
	cin.close()
	return input,output
	
#renvoie le rang correspondant au debut des colonnes 
#correspondants aux variables cibles
def searchCible(file):
	cin= open(file, 'r')
	line= cin.readline() # Skip the first line
	cin.close()
	while(len(line) > 0):
		p = line.replace('\n', '').split(';')
		for i in range(len(p)):
			if(str(p[i]).startswith("D_")):
				#print("debut ->"+str(i)+" ",str(p[i]))
				return i,len(p)-i-1
	
	
def loadData(filename_train ,filename_test ):
	trainInput = []
	trainOutput = []
	testInput = []
	testOutput = []
	beginCible,endcible=searchCible(filename_train)
	trainInput,trainOutput=openFile(filename_train,beginCible)
	testInput,testOutput=openFile(filename_test,beginCible)
	return np.array(trainInput), np.array(trainOutput), np.array(testInput), np.array(testOutput)


def precision(output, target, seuil = 0.5):
	tp = 0.0
	fp = 0.0
	for i in range(len(output)):
		if(output[i] > seuil): # output[i] = 1
			if(target[i] == 1):
				tp += 1.0
			else:
				fp += 1.0
	
	if(tp > 0 or fp > 0):
		return tp / (tp + fp)
	else:
		return 0.0

def recall(output, target, seuil = 0.5):
	tp = 0.0
	fn = 0.0
	for i in range(len(output)):
		if(output[i] > seuil and target[i] == 1):
			tp += 1.0
	for i in range(len(target)):
		if(target[i] == 1 and not(output[i] > seuil)):
			fn += 1
	
	if(tp > 0 or fn > 0):
		return tp / (tp + fn)
	else:
		return 0.0

def rmse(vec):
	# print(str(vec))
	# print(str(vec.shape))
	# exit(0)
	sum = 0
	for elt in vec:
		sum += (elt * elt)
	return sqrt(sum / len(vec))

def listeFileTrainAndTest():
	files=os.listdir('.')
	print(files)
	listFileTest  = [ f for f in files if ( str(f).startswith("test") and str(f).endswith(".csv") ) ]
	#print("listFileTest =>",listFileTest)
	listFileTrain = [ f for f in files if ( str(f).startswith("training") and str(f).endswith(".csv") ) ]
	#print("listFileTrain =>",listFileTrain)
	return listFileTrain,listFileTest

def evaluation(model,dataInputs,dataOutputs,seuil = 0.2):
      sum_precision = 0.0
      sum_recall = 0.0
      sum_rmse = 0.0
      for i in range(len(dataInputs)):
              input = dataInputs[i]
              target = dataOutputs[i]
              target = np.transpose(np.asmatrix(target))
              output = model.predict(input)
              sum_precision += precision(output, target,seuil)
              sum_recall += recall(output, target,seuil)
              error = np.subtract(target, output)
                      #error = soustrat(target, output)
              sum_rmse += rmse(error)
      avg_precision = sum_precision / len(dataInputs)
      avg_recall = sum_recall / len(dataInputs)
      avg_rmse = sum_rmse / len(dataInputs)
      return avg_precision,avg_recall,avg_rmse

def test():
      liste_de_valeur=[80000,100000,150000,200000,250000,300000,350000,400000,450000,500000,550000,600000,650000,700000,750000,800000,850000,900000,950000,1000000]
      #liste_de_valeur=[1]
      path = os. getcwd()
      listFileTrain,listFileTest=listeFileTrainAndTest()
      for nbre_iter in liste_de_valeur:
          avg_rmse_all=[]
          avg_rmse_all_train=[]
          avg_precision_all= []
          avg_recall_all = []
          for i in range(len(listFileTrain)):
              trainInputs, trainOutputs, testInputs, testOutputs = loadData(listFileTrain[i],listFileTest[i])
              enter,sortie=searchCible(listFileTrain[i])
              nn = NeuralNetwork(enter,9, sortie)
              nn.fit(trainInputs,trainOutputs, nbre_iter)
              ##nn.draw()
              avg_precision,avg_recall,avg_rmse=evaluation(nn,testInputs, testOutputs,0.2)
              avg_precision_train,avg_recall_train,avg_rmse_train=evaluation(nn,trainInputs, trainOutputs, 0.2)
              avg_rmse_all_train.append(avg_rmse_train)
              avg_rmse_all.append(avg_rmse)
              avg_recall_all.append(avg_recall)
              avg_precision_all.append(avg_precision)
              #print(' avg_rmse = ' + str(avg_rmse))
              #print(' avg_precision = ' + str(avg_precision))
              #print(' avg_recall = ' + str(avg_recall)) "DataSet"+
              #'rmse_train':avg_rmse_all_train ,
          result = pd.DataFrame({"modele":[str(p)[26:(len(p)-4)] for p in listFileTrain],'rmse_train':avg_rmse_all_train , 'rmse_test':avg_rmse_all ,
                                 'avg_recall':avg_recall_all,'avg_precision':avg_precision_all} )
          resultat= pd.DataFrame({"modele":[str(p)[9:(len(p)-4)] for p in listFileTrain],'rmse_train':avg_rmse_all_train , 'rmse_test':avg_rmse_all ,
                                 'avg_recall':avg_recall_all,'avg_precision':avg_precision_all} )
          #resultat=pd.DataFrame(
          returnValue = resultat.to_csv('resultats\\resultats_sur_'+str(nbre_iter)+'_iteration.csv',sep= ";"
                                        ,na_rep= "", header= True,mode= "w",index=False,encoding= "ISO-8859-1") 
          print(result)
      

       
	


test()
