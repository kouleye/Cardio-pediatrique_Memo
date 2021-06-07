#!/usr/bin/python
# -*- coding: latin-1 -*-
import os
from math import sqrt
import numpy as np
import pandas as pd
from neuralNetwork import NeuralNetwork
from copy import deepcopy
import seaborn as sns
#from neuralNetwork import soustrat
#from neuralNetwork import vec_seuil

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
#---------------methode of k fold beginning------------------
#compare deux liste et renvoie 0 si elles sont egale et 1 sinon
def compar(list1,list2):
  for e in list1==list2:
    if(e==False):
      return 1
  return 0

##division une liste en k-parties
def k_subdivision(input,k):
  len_part=len(input)//k
  for i in range(0, len(input),len_part):
        yield input[i:i + len_part]


#attribution de non a chaque partie
def created_var_part(generator,nom):
  dictionnary=dict()
  for i, part in enumerate(generator):
    dictionnary[str(nom)+"{0}".format(i)] = part
  return dictionnary

#remplacer un element dans une liste
#tout en gardant la trace de cette derniere
#trace_=list() 
#conteneur=list()
#trace contient les differents index deja utiliser pour effectuer le remplacement
#cette fonction renvoie une nouvelle partie qui sera remplace part celle 
#pris en entreea savoir part_=part_testInput_+part_testOutput_ qui est une partie du dataSet test
#envoye que des copies de vos variables en parametre pour eviter des surprises
def replace_elt(trainIn, trainOut,part_testIn,part_testOut,trace_):
    trainInput_copy=deepcopy(trainIn)
    new_part_testIn_=deepcopy(part_testIn)
    trace=deepcopy(trace_)
    for j,elt in enumerate(part_testOut):
        indices=list()
        if(elt in  trainOut):
            #je recupere toute les position ou elt apparait dans trainOutput
            indices = [i for i, x in enumerate(trainOut) if compar(x,elt)==0]
        stop=True
        cpt=0
        while(stop==True and cpt<len(indices)):
            if(indices[cpt] not in trace):
                new_part_testIn_[j]=trainIn[indices[cpt]]
                trace.append(indices[cpt])
                trainInput_copy[indices[cpt]]=part_testIn[j]
                stop=False
            cpt=cpt+1
    return trainInput_copy, new_part_testIn_,trace
      


#modification de la valeur d'un dictionnaire
def update_dict(dictionnary,part,name):
    diction=deepcopy(dictionnary)
    diction.update({name: part})
    return diction


#ecrire une fonction qui reunis les parties du test
def  join_part_test(dictionnary):
    result=[]
    for key, value in dictionnary.items():
        for elt in value:
            result.append(elt)
    return result


#---------------methode of k fold ending------------------

def test():
      path = os. getcwd()
      listFileTrain,listFileTest=listeFileTrainAndTest()
      liste_k=[2,3,4,5,6,7,8,9]
      #liste_k=[2,3,4]
      
      liste_de_valeur=[100000] #pour 9 neurones dans la couche cachee
      #liste_de_valeur=[100000,500000] #pour 8 neurones dans la couche cachee
      
      #liste_de_valeur=[1,2,3,4]
      for elt in liste_k:
              for nbre_iter in liste_de_valeur:
                      avg_rmse_all=[]
                      avg_rmse_all_train=[]
                      avg_precision_all= []
                      avg_recall_all = []
                      #k=int(input("entrer la valeur de k pour le processus de k-fold  :" ))
                      k=elt
                      print("vous allez faire un "+str(k)+"-fold validation")
                      for i in range(len(listFileTrain)):
                              trainInputs, trainOutputs, testInputs, testOutputs = loadData(listFileTrain[i],listFileTest[i])
                              enter,sortie=searchCible(listFileTrain[i])
                              nn = NeuralNetwork(enter,9, sortie)
                              ## tout debute ici pour le k fold validation
                              #subdivise le input test en k partie
                              dict_testInput=created_var_part(k_subdivision(testInputs,k),'part_testInput_')
                              dict_testOutput=created_var_part(k_subdivision(testOutputs,k),'part_testOutput_')
                              trace_=[]##permet d'eviter de changer une observation qui etait deja echanger au paravent
                              trainInput_copy=deepcopy(trainInputs)
                              trainOutput_copy=deepcopy(trainOutputs)

                              avg_rmse_tab=[]
                              avg_rmse_tab_train=[]
                              avg_precision_tab= []
                              avg_recall_tab = []

                              #cst1 et cst2 sont des tuples contenant respectivement des (cle,valeurs) dans le meme ordre
                              for cst1,cst2 in zip(dict_testInput.items(),dict_testOutput ):
                                      #j'effectue des copies pour eviter de perdre les listes de depart
                                      t_I_c, new_part_testIn_,trace=replace_elt(trainInput_copy,trainOutput_copy,cst1[1],cst2[1],trace_)
                                      trace_=trace_+trace

                                      #remplacement de l'ancienne partie par le nouveau
                                      new_dict=update_dict(dict_testInput,new_part_testIn_,cst1[0])

                                      #fusion valeurs du dictionnaire pour obtenir une liste favorable pour l'entrainement
                                      new_testIn =join_part_test(new_dict)
                                      new_testOut=join_part_test(dict_testOutput)
                                      nn.fit(t_I_c, trainOutput_copy, nbre_iter)

                                      avg_precision_,avg_recall_,avg_rmse_=evaluation(nn,new_testIn, new_testOut,0.2)
                                      avg_precision_tab.append(avg_precision_)
                                      avg_recall_tab.append(avg_recall_)
                                      avg_rmse_tab.append(avg_rmse_)

                                      avg_precision_train_,avg_recall_train_,avg_rmse_train_=evaluation(nn,t_I_c, trainOutput_copy, 0.2)
                                      avg_rmse_tab_train.append(avg_rmse_train_)

                              avg_rmse=sum(avg_rmse_tab)/k
                              avg_rmse_train=sum(avg_rmse_tab_train)/k
                              avg_recall=sum(avg_recall_tab)/k
                              avg_precision=sum(avg_precision_tab)/k

                              #fin de k-fold

                              avg_rmse_all_train.append(avg_rmse_train)
                              avg_rmse_all.append(avg_rmse)
                              avg_recall_all.append(avg_recall)
                              avg_precision_all.append(avg_precision)

               #'rmse_train':avg_rmse_all_train ,
                      result = pd.DataFrame({"modele":[str(p)[26:(len(p)-4)] for p in listFileTrain], 'rmse_test':avg_rmse_all ,'avg_recall':avg_recall_all,'avg_precision':avg_precision_all} )
                      print(result)#'validation_result\\'+'k_'+str(k)+'\\

                      resultat= pd.DataFrame({"modele":[str(p)[9:(len(p)-4)] for p in listFileTrain], 'rmse_test':avg_rmse_all ,
                                 'avg_recall':avg_recall_all,'avg_precision':avg_precision_all} )
                      returnValue = resultat.to_csv('validation_result\\'+'k_'+str(k)+'\\_'+str(nbre_iter)+'_iteration.csv',sep= ";",na_rep= "", header= True,mode= "w",index=False,encoding= "ISO-8859-1")
      

       
	


test()
