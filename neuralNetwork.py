#!/usr/bin/python
# -*- coding: latin-1 -*-

import numpy as np
import random
from graphviz import Digraph

class NeuralNetwork:
	def __init__(self, i, h, o, l_rate = 0.01):
		self.INPUT_LAYER_SIZE = i
		self.HIDDEN_LAYER_SIZE = h
		self.OUTPUT_LAYER_SIZE = o
		# ?
		self.weights_ih = np.random.rand(h, i)
		self.weights_ho = np.random.rand(o, h)
		self.bias_h = np.random.rand(h, 1)
		self.bias_o = np.random.rand(o, 1)
		
		self.weights_ih = self.weights_ih*2 -1
		self.weights_ho = self.weights_ho*2 -1
		self.bias_h = self.bias_h*2 -1
		self.bias_o = self.bias_o*2 -1
		
		self.l_rate = l_rate
	
	# See: https://stackoverflow.com/questions/43024745/applying-a-function-along-a-numpy-array
	def sigmoid(self, x):
		return 1 / (1 + np.exp(-x))
	
	def dsigmoid(self, y):
		return np.multiply(y, (1.0 - y))
	
	def predict(self, input):
		# ??
		input = np.transpose(np.asmatrix(input))
		# ??
		hidden = np.dot(self.weights_ih, input)
		hidden = np.add(hidden, self.bias_h)
		hidden = self.sigmoid(hidden)
		# ??
		output = np.dot(self.weights_ho, hidden)
		output = np.add(output, self.bias_o)
		output = self.sigmoid(output)
		# ??
		return output
	
	def train(self, input, target):
		input = np.transpose(np.asmatrix(input))
		target = np.transpose(np.asmatrix(target))
		
		hidden = np.dot(self.weights_ih, input)
		hidden = np.add(hidden, self.bias_h)
		hidden = self.sigmoid(hidden)
		
		output = np.dot(self.weights_ho, hidden)
		output = np.add(output, self.bias_o)
		output = self.sigmoid(output)
		
		error = np.subtract(target, output)
		gradient = self.dsigmoid(output)
		gradient = np.multiply(gradient, error)
		gradient = np.multiply(gradient, self.l_rate)
		
		hidden_T = np.transpose(hidden)
		who_delta = np.dot(gradient, hidden_T)
		
		self.weights_ho = np.add(self.weights_ho, who_delta)
		self.bias_o = np.add(self.bias_o, gradient)
		
		who_T = np.transpose(self.weights_ho)
		hidden_errors = np.dot(who_T, error)

		h_gradient = self.dsigmoid(hidden)
		h_gradient = np.multiply(h_gradient, hidden_errors)
		h_gradient = np.multiply(h_gradient, self.l_rate)

		i_T = np.transpose(input)
		wih_delta = np.dot(h_gradient, i_T)

		self.weights_ih = np.add(self.weights_ih, wih_delta)
		self.bias_h = np.add(self.bias_h, h_gradient)
	
	def fit(self, inputs, targets, epochs):
		nb = epochs // 100
		steps = [i for i in range(0, nb)]
		
		for i in range(1, 101):
			for s in steps: 
				draw = random.randint(0, len(inputs)-1) 
				input = inputs[draw] 
				target = targets[draw] 
				self.train(input, target)
			print(" -- " + str(i) + "% ... ")
		print(" -- Training done")
	
	def draw(self):
		_fontsize = 52
		_fontname = 'Tahoma'
		f = Digraph('finite_state_machine', filename='./fsm.gv')
		# f.attr(rankdir='LR', page='8.27,11.67', size='8', imagescale='both', splines='true')
		f.attr(rankdir='LR', size='6,4', ratio='fill', splines='true')
		
		for i in range(self.INPUT_LAYER_SIZE):
			f.node(name='i' + str(i), label='<I<sub>' + str(i) + '</sub>>', shape='circle', color='chartreuse', style='filled', fillcolor='chartreuse', fontname='Tahoma', fontsize='52')
		
		for i in range(self.HIDDEN_LAYER_SIZE):
			f.node(name='h' + str(i), label='<H<sub>' + str(i) + '</sub>>', shape='circle', color='dodgerblue', style='filled', fillcolor='dodgerblue', fontname='Tahoma', fontsize='52')
		
		for i in range(self.OUTPUT_LAYER_SIZE):
			f.node(name='o' + str(i), label='<O<sub>' + str(i) + '</sub>>', shape='circle', color='coral1', style='filled', fillcolor='coral1', fontname='Tahoma', fontsize='52')
		
		for i in range(self.INPUT_LAYER_SIZE):
			for h in range(self.HIDDEN_LAYER_SIZE):
				v = str(self.weights_ih[h,i])[:10]
				f.edge('i' + str(i), 'h' + str(h), label=v, fontname='Tahoma', fontsize='72')
		
		for h in range(self.HIDDEN_LAYER_SIZE):
			for o in range(self.OUTPUT_LAYER_SIZE):
				v = str(self.weights_ho[o,h])[:10]
				f.edge('h' + str(h), 'o' + str(o), label=v, fontname='Tahoma', fontsize='72')
		
		f.view()


def main():
	inputs = np.array([[1,1,1, 1,0,1, 1,0,1, 1,0,1, 1,1,1], [1,1,0, 0,1,0, 0,1,0, 0,1,0, 1,1,1], [1,1,1, 0,0,1, 1,1,1, 1,0,0, 1,1,1], [1,1,1, 0,0,1, 0,1,1, 0,0,1, 1,1,1], [0,1,1, 1,0,1, 1,1,1, 0,0,1, 0,0,1], [1,1,1, 1,0,0, 1,1,1, 0,0,1, 1,1,1], [1,1,1, 1,0,0, 1,1,1, 1,0,1, 1,1,1], [1,1,0, 0,1,0, 1,1,1, 0,1,0, 0,1,0], [1,1,1, 1,0,1, 1,1,1, 1,0,1, 1,1,1], [1,1,1, 1,0,1, 1,1,1, 0,0,1, 1,1,1]])
	
	targets = np.array([[1, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 1, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 1, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 1, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 1, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 1, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 1, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 1, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 1, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 1]])
	
	nn = NeuralNetwork(15, 8, 10)
	nn.fit(inputs, targets, 200000)
	nn.draw()
	
	inputs = np.array([[1,1,1, 1,0,1, 1,0,1, 1,0,1, 1,1,1], [1,1,0, 0,1,0, 0,1,0, 0,1,0, 1,1,1], [1,1,1, 0,0,1, 1,0,1, 1,0,0, 1,1,1], [1,1,1, 0,0,1, 0,1,1, 0,0,1, 1,1,1], [1,1,1, 1,0,1, 1,0,1, 0,0,1, 1,1,1]])
	
	for input in inputs:
		output = nn.predict(input)
		print(input)
		print(output)
		print(" ---------------- ")

def main2():
	X = np.array([[0,0], [0,1], [1,0], [1,1]])
	
	Y = np.array([[1, 0], [0, 1], [0, 1], [1, 0]])
	
	nn = NeuralNetwork(2, 3, 2)
	
	nn.fit(X, Y, 2000)
	
	inputs = np.array([[0,0], [0,1], [1,0], [1,1]])
	
	for input in inputs:
		output = nn.predict(input)
		print(input)
		print(output)
		print(" ---------------- ")

#main()
